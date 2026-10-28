package com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactosPorIdsFinder;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantesPorIdsFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudiantesPertenenciaProyectoGradoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudiantesYaVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.entity.EstudianteArtefactoEntity;
import com.arquisoft.artefactos.application.estudianteartefacto.command.validator.AsignarEstudiantesArtefactoValidator;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionesRepositorioArtefactoPorIdsFinder;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.EstudianteArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.event.EstudiantesArtefactoAsignadosEvent;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoEstudiante;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ParEstudianteArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;
import com.arquisoft.shared.publisher.EventPublisher;
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
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignarEstudiantesArtefactoUseCaseImplTest {

    @Mock
    private ArtefactosPorIdsFinder artefactosPorIdsFinder;
    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;
    @Mock
    private VersionesRepositorioArtefactoPorIdsFinder versionesRepositorioArtefactoPorIdsFinder;
    @Mock
    private EstudiantesPorIdsFinder estudiantesPorIdsFinder;
    @Mock
    private EstudiantesPertenenciaProyectoGradoFinder estudiantesPertenenciaProyectoGradoFinder;
    @Mock
    private EstudiantesYaVinculadosArtefactoFinder estudiantesYaVinculadosArtefactoFinder;
    @Mock
    private AsignarEstudiantesArtefactoValidator asignarEstudiantesArtefactoValidator;
    @Mock
    private EstudianteArtefactoOutputPort estudianteArtefactoOutputPort;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    @Captor
    private ArgumentCaptor<List<EstudianteArtefactoEntity>> entidadesCaptor;

    private AsignarEstudiantesArtefactoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AsignarEstudiantesArtefactoUseCaseImpl(
                artefactosPorIdsFinder, asesorPorIdFinder, versionesRepositorioArtefactoPorIdsFinder,
                estudiantesPorIdsFinder, estudiantesPertenenciaProyectoGradoFinder,
                estudiantesYaVinculadosArtefactoFinder, asignarEstudiantesArtefactoValidator,
                estudianteArtefactoOutputPort, eventPublisher, logger);
    }

    private static List<UUID> ids(int cantidad) {
        return IntStream.range(0, cantidad).mapToObj(i -> UtilUUID.generarNuevoUUID()).toList();
    }

    private static VersionRepositorioArtefactoDomain version(String nombreDocumento) {
        return VersionRepositorioArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), nombreDocumento, 1, UtilUUID.generarNuevoUUID(), "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
    }

    @Test
    void debeConsultarUnaVezValidarVincularYPublicar_cuandoTodoEsValido() {
        // Arrange
        var asesorId = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var idsArtefactos = ids(3);
        var idsEstudiantes = ids(3);
        var entrada = AgregacionEstudiantesArtefactoDomain.crear(
                asesorId, EstudianteArtefactoDomain.crear(idsArtefactos, idsEstudiantes));
        var versiones = idsArtefactos.stream().map(id -> version("Doc-" + id)).toList();
        var artefactos = IntStream.range(0, 3)
                .mapToObj(i -> ArtefactoDomain.reconstruir(
                        idsArtefactos.get(i), proyecto, versiones.get(i).getId(), asesorId))
                .toList();
        var asesor = AsesorDomain.reconstruir(asesorId, "1", "Ana Perez", "ana@uco.edu.co", Instant.now(), null);
        var estudiantes = idsEstudiantes.stream()
                .map(id -> EstudianteDomain.reconstruir(id, "2", "Est-" + id, id + "@uco.edu.co", Instant.EPOCH, null))
                .toList();
        var pertenencias = entrada.getPares();
        var yaVinculados = List.<ParEstudianteArtefacto>of();
        when(artefactosPorIdsFinder.obtener(idsArtefactos)).thenReturn(artefactos);
        when(asesorPorIdFinder.obtener(asesorId)).thenReturn(asesor);
        when(versionesRepositorioArtefactoPorIdsFinder.obtener(anyList())).thenReturn(versiones);
        when(estudiantesPorIdsFinder.obtener(idsEstudiantes)).thenReturn(estudiantes);
        when(estudiantesPertenenciaProyectoGradoFinder.obtener(entrada)).thenReturn(pertenencias);
        when(estudiantesYaVinculadosArtefactoFinder.obtener(entrada)).thenReturn(yaVinculados);

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var orden = inOrder(
                artefactosPorIdsFinder, asesorPorIdFinder, versionesRepositorioArtefactoPorIdsFinder,
                estudiantesPorIdsFinder, estudiantesPertenenciaProyectoGradoFinder,
                estudiantesYaVinculadosArtefactoFinder, asignarEstudiantesArtefactoValidator,
                estudianteArtefactoOutputPort, eventPublisher);
        orden.verify(artefactosPorIdsFinder).obtener(idsArtefactos);
        orden.verify(asesorPorIdFinder).obtener(asesorId);
        orden.verify(versionesRepositorioArtefactoPorIdsFinder)
                .obtener(artefactos.stream().map(ArtefactoDomain::getVersionRepositorioArtefacto).toList());
        orden.verify(estudiantesPorIdsFinder).obtener(idsEstudiantes);
        orden.verify(estudiantesPertenenciaProyectoGradoFinder).obtener(entrada);
        orden.verify(estudiantesYaVinculadosArtefactoFinder).obtener(entrada);
        orden.verify(asignarEstudiantesArtefactoValidator)
                .validar(entrada, artefactos, asesor, versiones, estudiantes, pertenencias, yaVinculados);
        orden.verify(estudianteArtefactoOutputPort).vincularTodos(anyList());
        orden.verify(eventPublisher).publish(any(EstudiantesArtefactoAsignadosEvent.class));
        verify(artefactosPorIdsFinder, times(1)).obtener(anyList());
        verify(versionesRepositorioArtefactoPorIdsFinder, times(1)).obtener(anyList());
        verify(estudiantesPorIdsFinder, times(1)).obtener(anyList());
        verify(estudiantesPertenenciaProyectoGradoFinder, times(1)).obtener(any());
        verify(estudiantesYaVinculadosArtefactoFinder, times(1)).obtener(any());
        verify(eventPublisher, times(1)).publish(any());
        verify(estudianteArtefactoOutputPort, times(1)).vincularTodos(entidadesCaptor.capture());
        assertThat(entidadesCaptor.getValue()).hasSize(9).allSatisfy(entity -> {
            assertThat(entity.id()).isNotNull();
            assertThat(idsArtefactos).contains(entity.artefacto());
            assertThat(idsEstudiantes).contains(entity.estudiante());
        });
        verify(logger).info(eq(EstudianteArtefactoKey.LOG_ASIGNADOS), eq(3), eq(3));
    }

    @Test
    void debeArmarEventoConDocumentosContactosYAsesor_cuandoVinculaEstudiantes() {
        // Arrange
        var asesorId = UtilUUID.generarNuevoUUID();
        var estudianteId = UtilUUID.generarNuevoUUID();
        var artefactoId = UtilUUID.generarNuevoUUID();
        var entrada = AgregacionEstudiantesArtefactoDomain.crear(
                asesorId, EstudianteArtefactoDomain.crear(List.of(artefactoId), List.of(estudianteId)));
        var version = version("Anteproyecto");
        var artefacto = ArtefactoDomain.reconstruir(
                artefactoId, UtilUUID.generarNuevoUUID(), version.getId(), asesorId);
        var asesor = AsesorDomain.reconstruir(asesorId, "1", "Ana Perez", "ana@uco.edu.co", Instant.now(), null);
        var estudiante = EstudianteDomain.reconstruir(estudianteId, "2", "Laura Gomez", "l@uco.edu.co", Instant.EPOCH, null);
        when(artefactosPorIdsFinder.obtener(anyList())).thenReturn(List.of(artefacto));
        when(asesorPorIdFinder.obtener(asesorId)).thenReturn(asesor);
        when(versionesRepositorioArtefactoPorIdsFinder.obtener(anyList())).thenReturn(List.of(version));
        when(estudiantesPorIdsFinder.obtener(anyList())).thenReturn(List.of(estudiante));
        when(estudiantesPertenenciaProyectoGradoFinder.obtener(entrada)).thenReturn(entrada.getPares());
        when(estudiantesYaVinculadosArtefactoFinder.obtener(entrada)).thenReturn(List.of());

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var captor = ArgumentCaptor.forClass(EstudiantesArtefactoAsignadosEvent.class);
        verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().getDocumentos()).containsExactly("Anteproyecto");
        assertThat(captor.getValue().getEstudiantes())
                .containsExactly(new ContactoEstudiante("Laura Gomez", "l@uco.edu.co"));
        assertThat(captor.getValue().getAsesor()).isEqualTo(new ContactoAsesor("Ana Perez", "ana@uco.edu.co"));
    }

    @Test
    void debeNoPersistirNiPublicarNiCerrar_cuandoElValidatorLanza() {
        // Arrange
        var asesorId = UtilUUID.generarNuevoUUID();
        var artefactoId = UtilUUID.generarNuevoUUID();
        var entrada = AgregacionEstudiantesArtefactoDomain.crear(
                asesorId, EstudianteArtefactoDomain.crear(List.of(artefactoId), List.of(UtilUUID.generarNuevoUUID())));
        when(artefactosPorIdsFinder.obtener(anyList())).thenReturn(List.of());
        when(asesorPorIdFinder.obtener(asesorId)).thenReturn(AsesorDomain.VACIO);
        when(versionesRepositorioArtefactoPorIdsFinder.obtener(anyList())).thenReturn(List.of());
        when(estudiantesPorIdsFinder.obtener(anyList())).thenReturn(List.of());
        when(estudiantesPertenenciaProyectoGradoFinder.obtener(entrada)).thenReturn(List.of());
        when(estudiantesYaVinculadosArtefactoFinder.obtener(entrada)).thenReturn(List.of());
        doThrow(new AsesorNoPropietarioArtefactoException(asesorId, artefactoId))
                .when(asignarEstudiantesArtefactoValidator)
                .validar(any(), anyList(), any(), anyList(), anyList(), anyList(), anyList());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
        verify(estudianteArtefactoOutputPort, never()).vincularTodos(any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(1), eq(1));
    }
}
