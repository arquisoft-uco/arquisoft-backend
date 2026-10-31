package com.arquisoft.artefactos.application.observacion.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorObservacionFinder;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantesVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionPorIdFinder;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.application.observacion.command.validator.CambiarEstadoAsesorObservacionValidator;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorPorObservacionFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.VersionArtefactoPorIdFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.observacion.CambioEstadoAsesorObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.event.ObservacionReabiertaEvent;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.CambiarEstadoAsesorObservacionKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
class CambiarEstadoAsesorObservacionUseCaseImplTest {

    @Mock
    private RevisionAsesorPorObservacionFinder revisionAsesorPorObservacionFinder;
    @Mock
    private ObservacionPorIdFinder observacionPorIdFinder;
    @Mock
    private ArtefactoPorObservacionFinder artefactoPorObservacionFinder;
    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;
    @Mock
    private VersionArtefactoPorIdFinder versionArtefactoPorIdFinder;
    @Mock
    private VersionRepositorioArtefactoPorIdFinder versionRepositorioArtefactoPorIdFinder;
    @Mock
    private EstudiantesVinculadosArtefactoFinder estudiantesVinculadosArtefactoFinder;
    @Mock
    private CambiarEstadoAsesorObservacionValidator validator;
    @Mock
    private ObservacionOutputPort observacionOutputPort;
    @Mock
    private RevisionAsesorOutputPort revisionAsesorOutputPort;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private CambiarEstadoAsesorObservacionUseCaseImpl useCase;

    private UUID observacionId;
    private UUID revisionId;
    private UUID asesorId;
    private UUID versionId;
    private UUID versionRepositorioId;
    private ObservacionDomain observacion;
    private ArtefactoDomain artefacto;
    private AsesorDomain asesor;
    private VersionArtefactoDomain version;
    private VersionRepositorioArtefactoDomain versionRepositorio;

    @BeforeEach
    void prepararEscenario() {
        observacionId = UtilUUID.generarNuevoUUID();
        revisionId = UtilUUID.generarNuevoUUID();
        asesorId = UtilUUID.generarNuevoUUID();
        versionId = UtilUUID.generarNuevoUUID();
        versionRepositorioId = UtilUUID.generarNuevoUUID();
        observacion = ObservacionDomain.reconstruir(
                observacionId, revisionId, "Falta la bibliografia", EstadoObservacion.SOLUCIONADA);
        artefacto = ArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), versionRepositorioId, asesorId, null);
        asesor = AsesorDomain.reconstruir(
                asesorId, "20161020123", "Ana Perez", "ana@uco.edu.co", UtilFecha.generarInstanteActual(), null);
        version = VersionArtefactoDomain.reconstruir(
                versionId, artefacto.getId(), 3, UtilFecha.generarInstanteActual(), "uri");
        versionRepositorio = VersionRepositorioArtefactoDomain.reconstruir(
                versionRepositorioId, "Anteproyecto", 1, UtilUUID.generarNuevoUUID(), "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));

        when(observacionPorIdFinder.obtener(observacionId)).thenReturn(observacion);
        when(artefactoPorObservacionFinder.obtener(observacionId)).thenReturn(artefacto);
        when(asesorPorIdFinder.obtener(asesorId)).thenReturn(asesor);
        when(versionArtefactoPorIdFinder.obtener(versionId)).thenReturn(version);
        when(versionRepositorioArtefactoPorIdFinder.obtener(versionRepositorioId)).thenReturn(versionRepositorio);
    }

    private RevisionAsesorDomain revision(EstadoRevisionAsesor estado) {
        var revision = RevisionAsesorDomain.reconstruir(
                revisionId, versionId, estado, UtilFecha.generarInstanteActual());
        when(revisionAsesorPorObservacionFinder.obtener(observacionId)).thenReturn(revision);
        return revision;
    }

    private List<EstudianteDomain> estudiantes(int cantidad) {
        return java.util.stream.IntStream.range(0, cantidad)
                .mapToObj(indice -> EstudianteDomain.reconstruir(UtilUUID.generarNuevoUUID(), "id" + indice,
                        "Estudiante " + indice, "est" + indice + "@uco.edu.co", UtilFecha.VACIO, null))
                .toList();
    }

    private CambioEstadoAsesorObservacionDomain cambio(String estado) {
        return CambioEstadoAsesorObservacionDomain.crear(observacionId, estado, asesorId);
    }

    @Test
    void debeCerrarSinTocarRevisionNiPublicar_cuandoDestinoCerrada() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.RESUELTA);
        when(estudiantesVinculadosArtefactoFinder.obtener(artefacto.getId())).thenReturn(estudiantes(3));
        var cambio = cambio("CERRADA");

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(revisionAsesorPorObservacionFinder, observacionPorIdFinder,
                artefactoPorObservacionFinder, asesorPorIdFinder, versionArtefactoPorIdFinder,
                versionRepositorioArtefactoPorIdFinder, estudiantesVinculadosArtefactoFinder, validator,
                observacionOutputPort, logger);
        orden.verify(logger).info(CambiarEstadoAsesorObservacionKey.LOG_CAMBIANDO_ESTADO,
                observacionId, asesorId, "CERRADA");
        orden.verify(revisionAsesorPorObservacionFinder, times(1)).obtener(observacionId);
        orden.verify(observacionPorIdFinder, times(1)).obtener(observacionId);
        orden.verify(artefactoPorObservacionFinder, times(1)).obtener(observacionId);
        orden.verify(asesorPorIdFinder, times(1)).obtener(asesorId);
        orden.verify(versionArtefactoPorIdFinder, times(1)).obtener(versionId);
        orden.verify(versionRepositorioArtefactoPorIdFinder, times(1)).obtener(versionRepositorioId);
        orden.verify(estudiantesVinculadosArtefactoFinder, times(1)).obtener(artefacto.getId());
        orden.verify(validator).validar(cambio, observacion, revision, artefacto, asesor, version,
                versionRepositorio);
        orden.verify(observacionOutputPort).actualizarEstado(observacionId, "CERRADA");
        orden.verify(logger).info(CambiarEstadoAsesorObservacionKey.LOG_ESTADO_CAMBIADO, observacionId, "CERRADA");
        verify(revisionAsesorOutputPort, never()).actualizarEstado(any(), anyString());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeReabrirYPasarRevisionAEnProgreso_cuandoRevisionResuelta() {
        // Arrange
        revision(EstadoRevisionAsesor.RESUELTA);
        when(estudiantesVinculadosArtefactoFinder.obtener(artefacto.getId())).thenReturn(estudiantes(1));

        // Act
        useCase.ejecutar(cambio("REABIERTA"));

        // Assert
        var orden = inOrder(observacionOutputPort, revisionAsesorOutputPort, eventPublisher);
        orden.verify(observacionOutputPort).actualizarEstado(observacionId, "REABIERTA");
        orden.verify(revisionAsesorOutputPort).actualizarEstado(revisionId, "EN_PROGRESO");
        orden.verify(eventPublisher).publish(any(ObservacionReabiertaEvent.class));
    }

    @Test
    void debeReabrirSinCambiarRevision_cuandoRevisionEnProgreso() {
        // Arrange
        revision(EstadoRevisionAsesor.EN_PROGRESO);
        when(estudiantesVinculadosArtefactoFinder.obtener(artefacto.getId())).thenReturn(estudiantes(1));

        // Act
        useCase.ejecutar(cambio("REABIERTA"));

        // Assert
        verify(observacionOutputPort).actualizarEstado(observacionId, "REABIERTA");
        verify(revisionAsesorOutputPort, never()).actualizarEstado(any(), anyString());
        verify(eventPublisher, times(1)).publish(any(ObservacionReabiertaEvent.class));
    }

    @Test
    void debePublicarEventoConContactosYDescripcion_cuandoReabierta() {
        // Arrange
        revision(EstadoRevisionAsesor.EN_PROGRESO);
        when(estudiantesVinculadosArtefactoFinder.obtener(artefacto.getId())).thenReturn(estudiantes(3));

        // Act
        useCase.ejecutar(cambio("REABIERTA"));

        // Assert
        var captor = ArgumentCaptor.forClass(ObservacionReabiertaEvent.class);
        verify(eventPublisher).publish(captor.capture());
        var evento = captor.getValue();
        assertThat(evento.getDocumento()).isEqualTo("Anteproyecto");
        assertThat(evento.getNumeroVersion()).isEqualTo(3);
        assertThat(evento.getAsesor().nombre()).isEqualTo("Ana Perez");
        assertThat(evento.getAsesor().email()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getEstudiantes()).hasSize(3);
        assertThat(evento.getEstudiantes().get(1).nombre()).isEqualTo("Estudiante 1");
        assertThat(evento.getEstudiantes().get(1).email()).isEqualTo("est1@uco.edu.co");
        assertThat(evento.getDescripcion()).isEqualTo("Falta la bibliografia");
    }

    @Test
    void noDebeEscribirNiPublicar_cuandoElValidatorRechaza() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.RESUELTA);
        when(estudiantesVinculadosArtefactoFinder.obtener(artefacto.getId())).thenReturn(estudiantes(1));
        var cambio = cambio("REABIERTA");
        doThrow(new AsesorNoPropietarioArtefactoException(asesorId, artefacto.getId())).when(validator)
                .validar(cambio, observacion, revision, artefacto, asesor, version, versionRepositorio);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
        verify(observacionOutputPort, never()).actualizarEstado(any(), anyString());
        verify(revisionAsesorOutputPort, never()).actualizarEstado(any(), anyString());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(eq(CambiarEstadoAsesorObservacionKey.LOG_ESTADO_CAMBIADO), any(), any());
    }
}
