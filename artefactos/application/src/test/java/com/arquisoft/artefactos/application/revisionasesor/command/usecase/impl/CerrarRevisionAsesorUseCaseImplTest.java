package com.arquisoft.artefactos.application.revisionasesor.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorRevisionAsesorFinder;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantesVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionesDeRevisionExistenFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionesVersionAnteriorSinCerrarExistenFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.validator.CerrarRevisionAsesorValidator;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.VersionArtefactoPorIdFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.revisionasesor.CierreRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.event.RevisionAsesorCerradaEvent;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesParaCierre;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.CerrarRevisionAsesorKey;
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
class CerrarRevisionAsesorUseCaseImplTest {

    @Mock
    private RevisionAsesorFinder revisionAsesorFinder;
    @Mock
    private ArtefactoPorRevisionAsesorFinder artefactoPorRevisionAsesorFinder;
    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;
    @Mock
    private VersionArtefactoPorIdFinder versionArtefactoPorIdFinder;
    @Mock
    private VersionRepositorioArtefactoPorIdFinder versionRepositorioArtefactoPorIdFinder;
    @Mock
    private EstudiantesVinculadosArtefactoFinder estudiantesVinculadosArtefactoFinder;
    @Mock
    private ObservacionesDeRevisionExistenFinder observacionesDeRevisionExistenFinder;
    @Mock
    private ObservacionesVersionAnteriorSinCerrarExistenFinder observacionesVersionAnteriorSinCerrarExistenFinder;
    @Mock
    private CerrarRevisionAsesorValidator validator;
    @Mock
    private RevisionAsesorOutputPort revisionAsesorOutputPort;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private CerrarRevisionAsesorUseCaseImpl useCase;

    private UUID revisionId;
    private UUID asesorId;
    private UUID versionId;
    private UUID versionRepositorioId;
    private RevisionAsesorDomain revision;
    private ArtefactoDomain artefacto;
    private AsesorDomain asesor;
    private VersionArtefactoDomain version;
    private VersionRepositorioArtefactoDomain versionRepositorio;
    private CierreRevisionAsesorDomain cierre;

    @BeforeEach
    void prepararEscenario() {
        revisionId = UtilUUID.generarNuevoUUID();
        asesorId = UtilUUID.generarNuevoUUID();
        versionId = UtilUUID.generarNuevoUUID();
        versionRepositorioId = UtilUUID.generarNuevoUUID();
        revision = RevisionAsesorDomain.reconstruir(
                revisionId, versionId, EstadoRevisionAsesor.PENDIENTE, UtilFecha.generarInstanteActual());
        artefacto = ArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), versionRepositorioId, asesorId, null);
        asesor = AsesorDomain.reconstruir(
                asesorId, "20161020123", "Ana Perez", "ana@uco.edu.co", UtilFecha.generarInstanteActual(), null);
        version = VersionArtefactoDomain.reconstruir(
                versionId, artefacto.getId(), 3, UtilFecha.generarInstanteActual(), "uri");
        versionRepositorio = VersionRepositorioArtefactoDomain.reconstruir(
                versionRepositorioId, "Anteproyecto", 1, UtilUUID.generarNuevoUUID(), "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
        cierre = CierreRevisionAsesorDomain.crear(revisionId, asesorId);

        when(revisionAsesorFinder.obtener(revisionId)).thenReturn(revision);
        when(artefactoPorRevisionAsesorFinder.obtener(revisionId)).thenReturn(artefacto);
        when(asesorPorIdFinder.obtener(asesorId)).thenReturn(asesor);
        when(versionArtefactoPorIdFinder.obtener(versionId)).thenReturn(version);
        when(versionRepositorioArtefactoPorIdFinder.obtener(versionRepositorioId)).thenReturn(versionRepositorio);
        when(observacionesDeRevisionExistenFinder.obtener(revisionId)).thenReturn(false);
        when(observacionesVersionAnteriorSinCerrarExistenFinder.obtener(revisionId)).thenReturn(false);
    }

    private List<EstudianteDomain> estudiantes(int cantidad) {
        return java.util.stream.IntStream.range(0, cantidad)
                .mapToObj(indice -> EstudianteDomain.reconstruir(UtilUUID.generarNuevoUUID(), "id" + indice,
                        "Estudiante " + indice, "est" + indice + "@uco.edu.co", UtilFecha.VACIO, null))
                .toList();
    }

    @Test
    void debeCerrarYPublicar_cuandoDatosValidos() {
        // Arrange
        when(estudiantesVinculadosArtefactoFinder.obtener(artefacto.getId())).thenReturn(estudiantes(3));

        // Act
        useCase.ejecutar(cierre);

        // Assert
        var orden = inOrder(revisionAsesorFinder, artefactoPorRevisionAsesorFinder, asesorPorIdFinder,
                versionArtefactoPorIdFinder, versionRepositorioArtefactoPorIdFinder,
                estudiantesVinculadosArtefactoFinder, observacionesDeRevisionExistenFinder,
                observacionesVersionAnteriorSinCerrarExistenFinder, validator, revisionAsesorOutputPort,
                eventPublisher, logger);
        orden.verify(logger).info(CerrarRevisionAsesorKey.LOG_CERRANDO, revisionId, asesorId);
        orden.verify(revisionAsesorFinder, times(1)).obtener(revisionId);
        orden.verify(artefactoPorRevisionAsesorFinder, times(1)).obtener(revisionId);
        orden.verify(asesorPorIdFinder, times(1)).obtener(asesorId);
        orden.verify(versionArtefactoPorIdFinder, times(1)).obtener(versionId);
        orden.verify(versionRepositorioArtefactoPorIdFinder, times(1)).obtener(versionRepositorioId);
        orden.verify(estudiantesVinculadosArtefactoFinder, times(1)).obtener(artefacto.getId());
        orden.verify(observacionesDeRevisionExistenFinder, times(1)).obtener(revisionId);
        orden.verify(observacionesVersionAnteriorSinCerrarExistenFinder, times(1)).obtener(revisionId);
        orden.verify(validator).validar(cierre, revision, artefacto, asesor, version, versionRepositorio,
                new ObservacionesParaCierre(false, false));
        orden.verify(revisionAsesorOutputPort).actualizarEstado(revisionId, "CERRADA");
        var captor = ArgumentCaptor.forClass(RevisionAsesorCerradaEvent.class);
        orden.verify(eventPublisher).publish(captor.capture());
        orden.verify(logger).info(CerrarRevisionAsesorKey.LOG_CERRADA, revisionId, 3);
        var evento = captor.getValue();
        assertThat(evento.getDocumento()).isEqualTo("Anteproyecto");
        assertThat(evento.getNumeroVersion()).isEqualTo(3);
        assertThat(evento.getAsesor().nombre()).isEqualTo("Ana Perez");
        assertThat(evento.getAsesor().email()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getEstudiantes()).hasSize(3);
        assertThat(evento.getEstudiantes().get(1).nombre()).isEqualTo("Estudiante 1");
        assertThat(evento.getEstudiantes().get(1).email()).isEqualTo("est1@uco.edu.co");
    }

    @Test
    void debePublicarConListaVacia_cuandoSinEstudiantes() {
        // Arrange
        when(estudiantesVinculadosArtefactoFinder.obtener(artefacto.getId())).thenReturn(List.of());

        // Act
        useCase.ejecutar(cierre);

        // Assert
        var captor = ArgumentCaptor.forClass(RevisionAsesorCerradaEvent.class);
        verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().getEstudiantes()).isEmpty();
        verify(revisionAsesorOutputPort).actualizarEstado(revisionId, "CERRADA");
        verify(logger).info(CerrarRevisionAsesorKey.LOG_CERRADA, revisionId, 0);
    }

    @Test
    void debeNoActualizarNiPublicar_cuandoValidatorRechaza() {
        // Arrange
        when(estudiantesVinculadosArtefactoFinder.obtener(artefacto.getId())).thenReturn(estudiantes(1));
        doThrow(new AsesorNoPropietarioArtefactoException(asesorId, artefacto.getId())).when(validator)
                .validar(cierre, revision, artefacto, asesor, version, versionRepositorio,
                        new ObservacionesParaCierre(false, false));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cierre))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
        verify(revisionAsesorOutputPort, never()).actualizarEstado(any(), anyString());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(eq(CerrarRevisionAsesorKey.LOG_CERRADA), any(), any());
    }
}
