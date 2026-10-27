package com.arquisoft.artefactos.application.revisionasesor.command.usecase.impl;

import com.arquisoft.artefactos.application.asesor.command.finder.AsesorDeRevisionAsesorFinder;
import com.arquisoft.artefactos.application.estudiante.command.finder.EstudianteFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.EstudianteParticipaRevisionAsesorFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.validator.CambiarEstadoRevisionAsesorValidator;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.revisionasesor.CambioEstadoRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.event.RevisionAsesorEstadoCambiadoEvent;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.message.key.artefactos.CambiarEstadoRevisionAsesorKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.Instant;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoRevisionAsesorUseCaseImplTest {

    @Mock
    private RevisionAsesorOutputPort revisionAsesorOutputPort;
    @Mock
    private RevisionAsesorFinder revisionAsesorFinder;
    @Mock
    private AsesorDeRevisionAsesorFinder asesorDeRevisionAsesorFinder;
    @Mock
    private EstudianteFinder estudianteFinder;
    @Mock
    private EstudianteParticipaRevisionAsesorFinder estudianteParticipaRevisionAsesorFinder;
    @Mock
    private CambiarEstadoRevisionAsesorValidator validator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private CambiarEstadoRevisionAsesorUseCaseImpl useCase;

    private UUID revisionId;
    private UUID estudianteId;
    private AsesorDomain asesor;
    private EstudianteDomain estudiante;

    @BeforeEach
    void prepararReplicas() {
        revisionId = UtilUUID.generarNuevoUUID();
        estudianteId = UtilUUID.generarNuevoUUID();
        asesor = AsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(), "20161020123", "Ana", "ana@uco.edu.co",
                Instant.now(), null);
        estudiante = EstudianteDomain.reconstruir(estudianteId, "Luis", "luis@uco.edu.co");
    }

    private void stubearFinders(EstadoRevisionAsesor estadoActual) {
        var revision = RevisionAsesorDomain.reconstruir(revisionId, UtilUUID.generarNuevoUUID(), estadoActual);
        when(revisionAsesorFinder.obtener(revisionId)).thenReturn(revision);
        when(asesorDeRevisionAsesorFinder.obtener(revisionId)).thenReturn(asesor);
        when(estudianteFinder.obtener(estudianteId)).thenReturn(estudiante);
        when(estudianteParticipaRevisionAsesorFinder.obtener(any())).thenReturn(true);
    }

    @Test
    void debeActualizarYPublicar_cuandoEstadoNuevoEsResuelta() {
        // Arrange
        stubearFinders(EstadoRevisionAsesor.EN_PROGRESO);
        var cambio = CambioEstadoRevisionAsesorDomain.crear(revisionId, "RESUELTA", estudianteId);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(revisionAsesorFinder, asesorDeRevisionAsesorFinder, estudianteFinder,
                estudianteParticipaRevisionAsesorFinder, validator, revisionAsesorOutputPort, eventPublisher);
        orden.verify(revisionAsesorFinder, times(1)).obtener(revisionId);
        orden.verify(asesorDeRevisionAsesorFinder, times(1)).obtener(revisionId);
        orden.verify(estudianteFinder, times(1)).obtener(estudianteId);
        orden.verify(estudianteParticipaRevisionAsesorFinder, times(1)).obtener(cambio);
        orden.verify(validator).validar(eq(cambio), any(RevisionAsesorDomain.class), eq(asesor), eq(estudiante),
                eq(true));
        orden.verify(revisionAsesorOutputPort).actualizarEstado(revisionId, "RESUELTA");
        var captor = ArgumentCaptor.forClass(RevisionAsesorEstadoCambiadoEvent.class);
        orden.verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().getRevisionAsesorId()).isEqualTo(revisionId);
        assertThat(captor.getValue().getEstado()).isEqualTo("RESUELTA");
        assertThat(captor.getValue().getAsesorNombre()).isEqualTo("Ana");
        assertThat(captor.getValue().getAsesorEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(captor.getValue().getEstudianteNombre()).isEqualTo("Luis");
        verify(logger).info(CambiarEstadoRevisionAsesorKey.LOG_ESTADO_CAMBIADO, revisionId, "RESUELTA");
    }

    @Test
    void debeActualizarSinPublicar_cuandoEstadoNuevoNoEsResuelta() {
        // Arrange
        stubearFinders(EstadoRevisionAsesor.PENDIENTE);
        var cambio = CambioEstadoRevisionAsesorDomain.crear(revisionId, "EN_PROGRESO", estudianteId);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        verify(revisionAsesorOutputPort).actualizarEstado(revisionId, "EN_PROGRESO");
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeCortarSinEscribirNiPublicar_cuandoEstadoIgualAlActual() {
        // Arrange
        stubearFinders(EstadoRevisionAsesor.RESUELTA);
        var cambio = CambioEstadoRevisionAsesorDomain.crear(revisionId, "RESUELTA", estudianteId);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        verify(validator).validar(eq(cambio), any(RevisionAsesorDomain.class), eq(asesor), eq(estudiante), eq(true));
        verify(revisionAsesorOutputPort, never()).actualizarEstado(any(), anyString());
        verify(eventPublisher, never()).publish(any());
        verify(logger).info(CambiarEstadoRevisionAsesorKey.LOG_ESTADO_SIN_CAMBIO, revisionId, "RESUELTA");
        verify(logger, never()).info(eq(CambiarEstadoRevisionAsesorKey.LOG_ESTADO_CAMBIADO), any(), any());
    }
}
