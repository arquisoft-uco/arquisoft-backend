package com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorIdFinder;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantesPorIdsFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudiantesPertenenciaProyectoGradoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudiantesYaVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.entity.EstudianteArtefactoEntity;
import com.arquisoft.artefactos.application.estudianteartefacto.command.validator.AsignarEstudiantesArtefactoValidator;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.EstudianteArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.event.EstudiantesArtefactoAsignadosEvent;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoEstudiante;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignarEstudiantesArtefactoUseCaseImplTest {

    @Mock
    private ArtefactoPorIdFinder artefactoPorIdFinder;
    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;
    @Mock
    private VersionRepositorioArtefactoPorIdFinder versionRepositorioArtefactoPorIdFinder;
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
    @Captor
    private ArgumentCaptor<EstudiantesArtefactoAsignadosEvent> eventoCaptor;

    private AsignarEstudiantesArtefactoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AsignarEstudiantesArtefactoUseCaseImpl(
                artefactoPorIdFinder, asesorPorIdFinder, versionRepositorioArtefactoPorIdFinder,
                estudiantesPorIdsFinder, estudiantesPertenenciaProyectoGradoFinder,
                estudiantesYaVinculadosArtefactoFinder, asignarEstudiantesArtefactoValidator,
                estudianteArtefactoOutputPort, eventPublisher, logger);
    }

    private static VersionRepositorioArtefactoDomain version(String nombreDocumento) {
        return VersionRepositorioArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), nombreDocumento, 1, UtilUUID.generarNuevoUUID(), "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
    }

    @Test
    void debeConsultarValidarVincularPublicarYLogear_cuandoTodoEsValido() {
        // Arrange
        var asesorId = UtilUUID.generarNuevoUUID();
        var artefactoId = UtilUUID.generarNuevoUUID();
        var idsEstudiantes = List.of(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var entrada = AgregacionEstudiantesArtefactoDomain.crear(
                asesorId, EstudianteArtefactoDomain.crear(artefactoId, idsEstudiantes));
        var version = version("Anteproyecto");
        var artefacto = ArtefactoDomain.reconstruir(
                artefactoId, UtilUUID.generarNuevoUUID(), version.getId(), asesorId);
        var asesor = AsesorDomain.reconstruir(asesorId, "1", "Ana Perez", "ana@uco.edu.co", Instant.now(), null);
        var estudiantes = idsEstudiantes.stream()
                .map(id -> EstudianteDomain.reconstruir(id, "2", "Est-" + id, id + "@uco.edu.co", Instant.EPOCH, null))
                .toList();
        when(artefactoPorIdFinder.obtener(artefactoId)).thenReturn(artefacto);
        when(asesorPorIdFinder.obtener(asesorId)).thenReturn(asesor);
        when(versionRepositorioArtefactoPorIdFinder.obtener(version.getId())).thenReturn(version);
        when(estudiantesPorIdsFinder.obtener(idsEstudiantes)).thenReturn(estudiantes);
        when(estudiantesPertenenciaProyectoGradoFinder.obtener(entrada)).thenReturn(idsEstudiantes);
        when(estudiantesYaVinculadosArtefactoFinder.obtener(entrada)).thenReturn(List.of());

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var orden = inOrder(
                artefactoPorIdFinder, asesorPorIdFinder, versionRepositorioArtefactoPorIdFinder,
                estudiantesPorIdsFinder, estudiantesPertenenciaProyectoGradoFinder,
                estudiantesYaVinculadosArtefactoFinder, asignarEstudiantesArtefactoValidator,
                estudianteArtefactoOutputPort, eventPublisher, logger);
        orden.verify(artefactoPorIdFinder).obtener(artefactoId);
        orden.verify(asesorPorIdFinder).obtener(asesorId);
        orden.verify(versionRepositorioArtefactoPorIdFinder).obtener(version.getId());
        orden.verify(estudiantesPorIdsFinder).obtener(idsEstudiantes);
        orden.verify(estudiantesPertenenciaProyectoGradoFinder).obtener(entrada);
        orden.verify(estudiantesYaVinculadosArtefactoFinder).obtener(entrada);
        orden.verify(asignarEstudiantesArtefactoValidator)
                .validar(entrada, artefacto, asesor, version, estudiantes, idsEstudiantes, List.of());
        orden.verify(estudianteArtefactoOutputPort).vincularTodos(entidadesCaptor.capture());
        orden.verify(eventPublisher).publish(eventoCaptor.capture());
        orden.verify(logger).info(EstudianteArtefactoKey.LOG_ASIGNADOS, artefactoId, 2);
        assertThat(entidadesCaptor.getValue()).hasSize(2).allSatisfy(entity -> {
            assertThat(entity.id()).isNotNull();
            assertThat(entity.artefacto()).isEqualTo(artefactoId);
            assertThat(idsEstudiantes).contains(entity.estudiante());
        });
        assertThat(eventoCaptor.getValue().getDocumento()).isEqualTo("Anteproyecto");
        assertThat(eventoCaptor.getValue().getEstudiantes()).containsExactlyElementsOf(estudiantes.stream()
                .map(e -> new ContactoEstudiante(e.getNombre(), e.getEmail())).toList());
        assertThat(eventoCaptor.getValue().getAsesor()).isEqualTo(new ContactoAsesor("Ana Perez", "ana@uco.edu.co"));
    }

    @Test
    void debeNoPersistirNiPublicarNiCerrar_cuandoElValidatorLanza() {
        // Arrange
        var asesorId = UtilUUID.generarNuevoUUID();
        var artefactoId = UtilUUID.generarNuevoUUID();
        var entrada = AgregacionEstudiantesArtefactoDomain.crear(
                asesorId, EstudianteArtefactoDomain.crear(artefactoId, List.of(UtilUUID.generarNuevoUUID())));
        when(artefactoPorIdFinder.obtener(artefactoId)).thenReturn(ArtefactoDomain.VACIO);
        when(asesorPorIdFinder.obtener(asesorId)).thenReturn(AsesorDomain.VACIO);
        when(versionRepositorioArtefactoPorIdFinder.obtener(any())).thenReturn(VersionRepositorioArtefactoDomain.VACIO);
        when(estudiantesPorIdsFinder.obtener(anyList())).thenReturn(List.of());
        when(estudiantesPertenenciaProyectoGradoFinder.obtener(entrada)).thenReturn(List.of());
        when(estudiantesYaVinculadosArtefactoFinder.obtener(entrada)).thenReturn(List.of());
        doThrow(new AsesorNoPropietarioArtefactoException(asesorId, artefactoId))
                .when(asignarEstudiantesArtefactoValidator)
                .validar(any(), any(), any(), any(), anyList(), anyList(), anyList());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
        verify(estudianteArtefactoOutputPort, never()).vincularTodos(any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(artefactoId), eq(1));
    }
}
