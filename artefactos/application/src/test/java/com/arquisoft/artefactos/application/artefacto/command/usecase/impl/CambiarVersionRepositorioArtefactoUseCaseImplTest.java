package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoBloqueadoPorIdFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.validator.CambiarVersionRepositorioArtefactoValidator;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantesVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionVigenteUltimaVersionFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.UltimoNumeroVersionArtefactoFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorArtefactoFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.CambioVersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.event.ArtefactoVersionRepositorioCambiadaEvent;
import com.arquisoft.artefactos.domain.artefacto.exception.CambioVersionRepositorioNoPermitidoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
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
class CambiarVersionRepositorioArtefactoUseCaseImplTest {

    @Mock
    private ArtefactoBloqueadoPorIdFinder artefactoBloqueadoPorIdFinder;
    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;
    @Mock
    private VersionRepositorioArtefactoPorArtefactoFinder versionRepositorioArtefactoPorArtefactoFinder;
    @Mock
    private VersionRepositorioArtefactoPorIdFinder versionRepositorioArtefactoPorIdFinder;
    @Mock
    private UltimoNumeroVersionArtefactoFinder ultimoNumeroVersionArtefactoFinder;
    @Mock
    private RevisionVigenteUltimaVersionFinder revisionVigenteUltimaVersionFinder;
    @Mock
    private EstudiantesVinculadosArtefactoFinder estudiantesVinculadosArtefactoFinder;
    @Mock
    private CambiarVersionRepositorioArtefactoValidator cambiarVersionRepositorioArtefactoValidator;
    @Mock
    private ArtefactoOutputPort artefactoOutputPort;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    @Captor
    private ArgumentCaptor<ArtefactoVersionRepositorioCambiadaEvent> eventoCaptor;
    @Captor
    private ArgumentCaptor<RevisionVersionAnterior> anteriorCaptor;

    private CambiarVersionRepositorioArtefactoUseCaseImpl useCase;

    private UUID artefactoId;
    private UUID asesorId;
    private UUID elegidaId;
    private CambioVersionRepositorioArtefactoDomain cambio;
    private ArtefactoDomain artefacto;
    private AsesorDomain asesor;
    private VersionRepositorioArtefactoDomain actual;
    private VersionRepositorioArtefactoDomain elegida;
    private RevisionAsesorDomain revision;

    @BeforeEach
    void setUp() {
        useCase = new CambiarVersionRepositorioArtefactoUseCaseImpl(
                artefactoBloqueadoPorIdFinder, asesorPorIdFinder, versionRepositorioArtefactoPorArtefactoFinder,
                versionRepositorioArtefactoPorIdFinder, ultimoNumeroVersionArtefactoFinder,
                revisionVigenteUltimaVersionFinder, estudiantesVinculadosArtefactoFinder,
                cambiarVersionRepositorioArtefactoValidator, artefactoOutputPort, eventPublisher, logger);
        artefactoId = UtilUUID.generarNuevoUUID();
        asesorId = UtilUUID.generarNuevoUUID();
        elegidaId = UtilUUID.generarNuevoUUID();
        var actualId = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        cambio = CambioVersionRepositorioArtefactoDomain.crear(artefactoId, asesorId, elegidaId);
        artefacto = ArtefactoDomain.reconstruir(artefactoId, UtilUUID.generarNuevoUUID(), actualId, asesorId, null);
        asesor = AsesorDomain.reconstruir(
                asesorId, "20161020123", "Ana Perez", "ana@uco.edu.co", UtilFecha.generarInstanteActual(), null);
        actual = VersionRepositorioArtefactoDomain.reconstruir(actualId, "Formato A", 1, repositorio, "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
        elegida = VersionRepositorioArtefactoDomain.reconstruir(elegidaId, "Formato A", 2, repositorio, "Repo",
                new DatosArchivo("b.pdf", "application/pdf", 10L));
        revision = RevisionAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.PENDIENTE,
                UtilFecha.generarInstanteActual());
        var estudiantes = List.of(
                EstudianteDomain.reconstruir(UtilUUID.generarNuevoUUID(), "1", "Laura Gomez", "laura@uco.edu.co",
                        UtilFecha.VACIO, null),
                EstudianteDomain.reconstruir(UtilUUID.generarNuevoUUID(), "2", "Pedro Diaz", "pedro@uco.edu.co",
                        UtilFecha.VACIO, null),
                EstudianteDomain.reconstruir(UtilUUID.generarNuevoUUID(), "3", "Sara Mora", "sara@uco.edu.co",
                        UtilFecha.VACIO, null));
        when(artefactoBloqueadoPorIdFinder.obtener(artefactoId)).thenReturn(artefacto);
        when(asesorPorIdFinder.obtener(asesorId)).thenReturn(asesor);
        when(versionRepositorioArtefactoPorArtefactoFinder.obtener(artefactoId)).thenReturn(actual);
        when(versionRepositorioArtefactoPorIdFinder.obtener(elegidaId)).thenReturn(elegida);
        when(ultimoNumeroVersionArtefactoFinder.obtener(artefactoId)).thenReturn(2);
        when(revisionVigenteUltimaVersionFinder.obtener(artefactoId)).thenReturn(revision);
        when(estudiantesVinculadosArtefactoFinder.obtener(artefactoId)).thenReturn(estudiantes);
    }

    @Test
    void debeCambiarYPublicarEvento_cuandoValidacionPasa() {
        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(artefactoBloqueadoPorIdFinder, asesorPorIdFinder,
                versionRepositorioArtefactoPorArtefactoFinder, versionRepositorioArtefactoPorIdFinder,
                ultimoNumeroVersionArtefactoFinder, revisionVigenteUltimaVersionFinder,
                estudiantesVinculadosArtefactoFinder, cambiarVersionRepositorioArtefactoValidator,
                artefactoOutputPort, eventPublisher, logger);
        orden.verify(logger).info(ArtefactoKey.LOG_CAMBIANDO_VERSION_REPOSITORIO, artefactoId, asesorId);
        orden.verify(artefactoBloqueadoPorIdFinder, times(1)).obtener(artefactoId);
        orden.verify(asesorPorIdFinder, times(1)).obtener(asesorId);
        orden.verify(versionRepositorioArtefactoPorArtefactoFinder, times(1)).obtener(artefactoId);
        orden.verify(versionRepositorioArtefactoPorIdFinder, times(1)).obtener(elegidaId);
        orden.verify(ultimoNumeroVersionArtefactoFinder, times(1)).obtener(artefactoId);
        orden.verify(revisionVigenteUltimaVersionFinder, times(1)).obtener(artefactoId);
        orden.verify(estudiantesVinculadosArtefactoFinder, times(1)).obtener(artefactoId);
        orden.verify(logger).debug(ArtefactoKey.LOG_VERIFICACION_CAMBIAR_VERSION_REPOSITORIO,
                true, true, true, true, 2, EstadoRevisionAsesor.PENDIENTE.getId(), 3);
        orden.verify(cambiarVersionRepositorioArtefactoValidator).validar(
                eq(cambio), eq(artefacto), eq(asesor), eq(actual), eq(elegida), anteriorCaptor.capture());
        orden.verify(artefactoOutputPort).cambiarVersionRepositorio(artefactoId, elegidaId);
        orden.verify(eventPublisher).publish(eventoCaptor.capture());
        orden.verify(logger).info(ArtefactoKey.LOG_VERSION_REPOSITORIO_CAMBIADA, artefactoId, elegidaId, 3);
        assertThat(anteriorCaptor.getValue().numeroVersion()).isEqualTo(2);
        assertThat(anteriorCaptor.getValue().revision()).isSameAs(revision);
        assertThat(artefacto.getVersionRepositorioArtefacto()).isEqualTo(elegidaId);
        var evento = eventoCaptor.getValue();
        assertThat(evento.getDocumento()).isEqualTo("Formato A");
        assertThat(evento.getVersionAnterior()).isEqualTo(1);
        assertThat(evento.getVersionNueva()).isEqualTo(2);
        assertThat(evento.getAsesor().nombre()).isEqualTo("Ana Perez");
        assertThat(evento.getAsesor().email()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getEstudiantes()).extracting(contacto -> contacto.nombre())
                .containsExactly("Laura Gomez", "Pedro Diaz", "Sara Mora");
        assertThat(evento.getEstudiantes()).extracting(contacto -> contacto.email())
                .containsExactly("laura@uco.edu.co", "pedro@uco.edu.co", "sara@uco.edu.co");
    }

    @Test
    void debeNoPersistirNiPublicar_cuandoValidatorLanza() {
        // Arrange
        doThrow(new CambioVersionRepositorioNoPermitidoException("Formato A", artefactoId, "RESUELTA"))
                .when(cambiarVersionRepositorioArtefactoValidator).validar(any(), any(), any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio))
                .isInstanceOf(CambioVersionRepositorioNoPermitidoException.class);
        verify(artefactoOutputPort, never()).cambiarVersionRepositorio(any(), any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(eq(ArtefactoKey.LOG_VERSION_REPOSITORIO_CAMBIADA), eq(artefactoId),
                eq(elegidaId), eq(3));
        assertThat(artefacto.getVersionRepositorioArtefacto()).isNotEqualTo(elegidaId);
    }
}
