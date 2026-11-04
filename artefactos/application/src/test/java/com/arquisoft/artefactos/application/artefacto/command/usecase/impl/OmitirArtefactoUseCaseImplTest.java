package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoBloqueadoPorIdFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.validator.OmitirArtefactoValidator;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantesVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorArtefactoFinder;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.OmisionArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.event.ArtefactoOmitidoEvent;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OmitirArtefactoUseCaseImplTest {

    @Mock
    private ArtefactoBloqueadoPorIdFinder artefactoBloqueadoPorIdFinder;
    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;
    @Mock
    private VersionRepositorioArtefactoPorArtefactoFinder versionRepositorioArtefactoPorArtefactoFinder;
    @Mock
    private EstudiantesVinculadosArtefactoFinder estudiantesVinculadosArtefactoFinder;
    @Mock
    private OmitirArtefactoValidator omitirArtefactoValidator;
    @Mock
    private ArtefactoOutputPort artefactoOutputPort;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    @Captor
    private ArgumentCaptor<ArtefactoOmitidoEvent> eventoCaptor;
    @Captor
    private ArgumentCaptor<Instant> instanteCaptor;

    private OmitirArtefactoUseCaseImpl useCase;

    private UUID artefactoId;
    private UUID asesorId;
    private OmisionArtefactoDomain omision;
    private ArtefactoDomain artefacto;
    private AsesorDomain asesor;
    private VersionRepositorioArtefactoDomain version;
    private List<EstudianteDomain> estudiantes;

    @BeforeEach
    void setUp() {
        useCase = new OmitirArtefactoUseCaseImpl(
                artefactoBloqueadoPorIdFinder, asesorPorIdFinder, versionRepositorioArtefactoPorArtefactoFinder,
                estudiantesVinculadosArtefactoFinder, omitirArtefactoValidator, artefactoOutputPort,
                eventPublisher, logger);
        artefactoId = UtilUUID.generarNuevoUUID();
        asesorId = UtilUUID.generarNuevoUUID();
        var versionId = UtilUUID.generarNuevoUUID();
        omision = OmisionArtefactoDomain.crear(artefactoId, asesorId);
        artefacto = ArtefactoDomain.reconstruir(artefactoId, UtilUUID.generarNuevoUUID(), versionId, asesorId, null);
        asesor = AsesorDomain.reconstruir(asesorId, "20161020123", "Ana Perez", "ana@uco.edu.co", UtilFecha.generarInstanteActual(), null);
        version = VersionRepositorioArtefactoDomain.reconstruir(
                versionId, "Formato A", 1, UtilUUID.generarNuevoUUID(), "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
        estudiantes = List.of(
                EstudianteDomain.reconstruir(UtilUUID.generarNuevoUUID(), "1", "Laura Gomez", "laura@uco.edu.co",
                        UtilFecha.VACIO, null),
                EstudianteDomain.reconstruir(UtilUUID.generarNuevoUUID(), "2", "Pedro Diaz", "pedro@uco.edu.co",
                        UtilFecha.VACIO, null),
                EstudianteDomain.reconstruir(UtilUUID.generarNuevoUUID(), "3", "Sara Mora", "sara@uco.edu.co",
                        UtilFecha.VACIO, null));
        when(artefactoBloqueadoPorIdFinder.obtener(artefactoId)).thenReturn(artefacto);
        when(asesorPorIdFinder.obtener(asesorId)).thenReturn(asesor);
        when(versionRepositorioArtefactoPorArtefactoFinder.obtener(artefactoId)).thenReturn(version);
        when(estudiantesVinculadosArtefactoFinder.obtener(artefactoId)).thenReturn(estudiantes);
    }

    @Test
    void debeOmitirYPublicarElEventoEnOrden_cuandoElFlujoEsExitoso() {
        // Act
        useCase.ejecutar(omision);

        // Assert
        var orden = inOrder(artefactoBloqueadoPorIdFinder, asesorPorIdFinder,
                versionRepositorioArtefactoPorArtefactoFinder, estudiantesVinculadosArtefactoFinder,
                omitirArtefactoValidator, artefactoOutputPort, eventPublisher, logger);
        orden.verify(logger).info(ArtefactoKey.LOG_OMITIENDO, artefactoId, asesorId);
        orden.verify(artefactoBloqueadoPorIdFinder, times(1)).obtener(artefactoId);
        orden.verify(asesorPorIdFinder, times(1)).obtener(asesorId);
        orden.verify(versionRepositorioArtefactoPorArtefactoFinder, times(1)).obtener(artefactoId);
        orden.verify(estudiantesVinculadosArtefactoFinder, times(1)).obtener(artefactoId);
        orden.verify(logger).debug(ArtefactoKey.LOG_VERIFICACION_OMITIR, true, true, true, 3);
        orden.verify(omitirArtefactoValidator).validar(omision, artefacto, asesor, version);
        orden.verify(artefactoOutputPort).omitir(eq(artefactoId), instanteCaptor.capture());
        orden.verify(eventPublisher).publish(eventoCaptor.capture());
        orden.verify(logger).info(ArtefactoKey.LOG_OMITIDO, artefactoId, asesorId);
        assertThat(artefacto.estaOmitido()).isTrue();
        assertThat(instanteCaptor.getValue()).isEqualTo(artefacto.getOmitidoEn()).isNotEqualTo(UtilFecha.VACIO);
        var evento = eventoCaptor.getValue();
        assertThat(evento.getDocumento()).isEqualTo("Formato A");
        assertThat(evento.getAsesor().nombre()).isEqualTo("Ana Perez");
        assertThat(evento.getAsesor().email()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getEstudiantes()).extracting(contacto -> contacto.nombre())
                .containsExactly("Laura Gomez", "Pedro Diaz", "Sara Mora");
        assertThat(evento.getEstudiantes()).extracting(contacto -> contacto.email())
                .containsExactly("laura@uco.edu.co", "pedro@uco.edu.co", "sara@uco.edu.co");
    }

    @Test
    void debeNoOmitirNiPublicar_cuandoElValidatorLanza() {
        // Arrange
        doThrow(new AsesorNoPropietarioArtefactoException(asesorId, artefactoId)).when(omitirArtefactoValidator)
                .validar(any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(omision))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
        verify(artefactoOutputPort, never()).omitir(any(), any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(eq(ArtefactoKey.LOG_OMITIDO), eq(artefactoId), eq(asesorId));
        assertThat(artefacto.estaOmitido()).isFalse();
    }
}
