package com.arquisoft.artefactos.application.observacion.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorObservacionFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.EstudianteParticipaObservacionFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionPorIdFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.model.ParticipacionEstudianteObservacion;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.application.observacion.command.validator.CambiarEstadoEstudianteObservacionValidator;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorPorObservacionFinder;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.observacion.CambioEstadoEstudianteObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoEditablePorEstudianteException;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.artefactos.CambiarEstadoEstudianteObservacionKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoEstudianteObservacionUseCaseImplTest {

    @Mock
    private RevisionAsesorPorObservacionFinder revisionAsesorPorObservacionFinder;
    @Mock
    private ObservacionPorIdFinder observacionPorIdFinder;
    @Mock
    private ArtefactoPorObservacionFinder artefactoPorObservacionFinder;
    @Mock
    private EstudianteParticipaObservacionFinder estudianteParticipaObservacionFinder;
    @Mock
    private CambiarEstadoEstudianteObservacionValidator validator;
    @Mock
    private ObservacionOutputPort observacionOutputPort;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private CambiarEstadoEstudianteObservacionUseCaseImpl useCase;

    private UUID observacionId;
    private UUID estudianteId;
    private RevisionAsesorDomain revision;
    private ArtefactoDomain artefacto;

    @BeforeEach
    void prepararEscenario() {
        observacionId = UtilUUID.generarNuevoUUID();
        estudianteId = UtilUUID.generarNuevoUUID();
        var revisionId = UtilUUID.generarNuevoUUID();
        revision = RevisionAsesorDomain.reconstruir(
                revisionId, UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.EN_PROGRESO,
                UtilFecha.generarInstanteActual());
        artefacto = ArtefactoDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), null);
    }

    private ObservacionDomain stubearFinders(EstadoObservacion estadoActual) {
        var observacion = ObservacionDomain.reconstruir(observacionId, revision.getId(), "Texto", estadoActual,
                UtilUUID.obtenerUUIDPorDefecto());
        when(revisionAsesorPorObservacionFinder.obtener(observacionId)).thenReturn(revision);
        when(observacionPorIdFinder.obtener(observacionId)).thenReturn(observacion);
        when(artefactoPorObservacionFinder.obtener(observacionId)).thenReturn(artefacto);
        when(estudianteParticipaObservacionFinder.obtener(
                new ParticipacionEstudianteObservacion(observacionId, estudianteId))).thenReturn(true);
        return observacion;
    }

    @Test
    void debeActualizarEstado_cuandoLaValidacionPasa() {
        // Arrange
        var observacion = stubearFinders(EstadoObservacion.EN_PROGRESO);
        var cambio = CambioEstadoEstudianteObservacionDomain.crear(observacionId, "SOLUCIONADA", estudianteId);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(revisionAsesorPorObservacionFinder, observacionPorIdFinder, artefactoPorObservacionFinder,
                estudianteParticipaObservacionFinder, validator, observacionOutputPort);
        orden.verify(revisionAsesorPorObservacionFinder, times(1)).obtener(observacionId);
        orden.verify(observacionPorIdFinder, times(1)).obtener(observacionId);
        orden.verify(artefactoPorObservacionFinder, times(1)).obtener(observacionId);
        orden.verify(estudianteParticipaObservacionFinder, times(1))
                .obtener(new ParticipacionEstudianteObservacion(observacionId, estudianteId));
        orden.verify(validator).validar(cambio, observacion, revision, artefacto, true);
        orden.verify(observacionOutputPort).actualizarEstado(observacionId, "SOLUCIONADA");
        verify(logger).info(CambiarEstadoEstudianteObservacionKey.LOG_ESTADO_CAMBIADO, observacionId, "SOLUCIONADA");
    }

    @Test
    void debeOmitirEscritura_cuandoElEstadoYaEsElDestino() {
        // Arrange
        var observacion = stubearFinders(EstadoObservacion.SOLUCIONADA);
        var cambio = CambioEstadoEstudianteObservacionDomain.crear(observacionId, "SOLUCIONADA", estudianteId);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        verify(validator).validar(cambio, observacion, revision, artefacto, true);
        verify(observacionOutputPort, never()).actualizarEstado(any(), anyString());
        verify(logger).info(CambiarEstadoEstudianteObservacionKey.LOG_ESTADO_SIN_CAMBIO, observacionId, "SOLUCIONADA");
        verify(logger, never()).info(
                eq(CambiarEstadoEstudianteObservacionKey.LOG_ESTADO_CAMBIADO), any(), any());
    }

    @Test
    void debePropagarRechazo_cuandoElValidatorLanza() {
        // Arrange
        stubearFinders(EstadoObservacion.CERRADA);
        var cambio = CambioEstadoEstudianteObservacionDomain.crear(observacionId, "SOLUCIONADA", estudianteId);
        doThrow(new ObservacionNoEditablePorEstudianteException(observacionId, EstadoObservacion.CERRADA))
                .when(validator).validar(any(), any(), any(), any(), eq(true));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio))
                .isInstanceOf(ObservacionNoEditablePorEstudianteException.class);
        verify(observacionOutputPort, never()).actualizarEstado(any(), anyString());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(observacionId), eq("SOLUCIONADA"));
    }
}
