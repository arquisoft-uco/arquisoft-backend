package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.RepositoriosAsignadosProyectoFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.artefactos.application.artefacto.command.validator.AsignarArtefactosValidator;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.AsignarEstudiantesArtefactoUseCase;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.VinculosMasRecientesProyectoGradoFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoExisteFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionesRepositorioArtefactoPorIdsFinder;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactosDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.EstudianteArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignarArtefactosUseCaseImplTest {

    @Mock
    private ProyectoGradoExisteFinder proyectoGradoExisteFinder;
    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;
    @Mock
    private VinculosMasRecientesProyectoGradoFinder vinculosMasRecientesProyectoGradoFinder;
    @Mock
    private VersionesRepositorioArtefactoPorIdsFinder versionesRepositorioArtefactoPorIdsFinder;
    @Mock
    private RepositoriosAsignadosProyectoFinder repositoriosAsignadosProyectoFinder;
    @Mock
    private AsignarArtefactosValidator asignarArtefactosValidator;
    @Mock
    private AsignarEstudiantesArtefactoUseCase asignarEstudiantesArtefactoUseCase;
    @Mock
    private ArtefactoOutputPort artefactoOutputPort;
    @Mock
    private AppLogger logger;

    @Captor
    private ArgumentCaptor<List<ArtefactoEntity>> entidadesCaptor;

    private AsignarArtefactosUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AsignarArtefactosUseCaseImpl(
                proyectoGradoExisteFinder, asesorPorIdFinder, vinculosMasRecientesProyectoGradoFinder,
                versionesRepositorioArtefactoPorIdsFinder, repositoriosAsignadosProyectoFinder,
                asignarArtefactosValidator, asignarEstudiantesArtefactoUseCase, artefactoOutputPort, logger);
    }

    private static AsignacionArtefactosDomain asignacion(UUID proyecto, UUID asesor, List<UUID> versiones) {
        var artefactos = ArtefactoDomain.crear(proyecto, asesor, versiones);
        return AsignacionArtefactosDomain.crear(artefactos, AgregacionEstudiantesArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(),
                EstudianteArtefactoDomain.crear(List.of(UtilUUID.generarNuevoUUID()), List.of(UtilUUID.generarNuevoUUID()))));
    }

    @Test
    void debeRegistrarTodosYRetornarIdsEnOrden_cuandoElValidatorAcepta() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var versiones = List.of(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var asignacion = asignacion(proyecto, asesor, versiones);
        var asesorDomain = AsesorDomain.VACIO;
        var vinculos = List.of(AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), asesor, proyecto, Instant.now(), null));
        var repositoriosAsignados = List.of(UtilUUID.generarNuevoUUID());
        when(proyectoGradoExisteFinder.obtener(proyecto)).thenReturn(true);
        when(asesorPorIdFinder.obtener(asesor)).thenReturn(asesorDomain);
        when(vinculosMasRecientesProyectoGradoFinder.obtener(proyecto))
                .thenReturn(vinculos);
        when(versionesRepositorioArtefactoPorIdsFinder.obtener(versiones)).thenReturn(List.of());
        when(repositoriosAsignadosProyectoFinder.obtener(proyecto)).thenReturn(repositoriosAsignados);

        // Act
        var resultado = useCase.ejecutar(asignacion);

        // Assert
        assertThat(resultado.artefactos())
                .containsExactlyElementsOf(asignacion.getArtefactos().stream().map(ArtefactoDomain::getId).toList());
        var orden = inOrder(
                proyectoGradoExisteFinder, asesorPorIdFinder, vinculosMasRecientesProyectoGradoFinder,
                versionesRepositorioArtefactoPorIdsFinder, repositoriosAsignadosProyectoFinder,
                asignarArtefactosValidator, artefactoOutputPort);
        orden.verify(proyectoGradoExisteFinder).obtener(proyecto);
        orden.verify(asesorPorIdFinder).obtener(asesor);
        orden.verify(vinculosMasRecientesProyectoGradoFinder).obtener(proyecto);
        orden.verify(versionesRepositorioArtefactoPorIdsFinder).obtener(versiones);
        orden.verify(repositoriosAsignadosProyectoFinder).obtener(proyecto);
        orden.verify(asignarArtefactosValidator)
                .validar(asignacion, true, asesorDomain, vinculos, List.of(), repositoriosAsignados);
        orden.verify(artefactoOutputPort).registrarTodos(anyList());
        verify(versionesRepositorioArtefactoPorIdsFinder, times(1)).obtener(anyList());
        verify(artefactoOutputPort, times(1)).registrarTodos(anyList());
        verify(artefactoOutputPort).registrarTodos(entidadesCaptor.capture());
        assertThat(entidadesCaptor.getValue()).hasSize(3).allSatisfy(entity -> {
            assertThat(entity.proyectoGrado()).isEqualTo(proyecto);
            assertThat(entity.asesor()).isEqualTo(asesor);
        });
        verify(logger).info(eq(ArtefactoKey.LOG_ASIGNADOS), eq(proyecto), eq(3));
        verify(asignarEstudiantesArtefactoUseCase, times(1)).ejecutar(asignacion.getEstudiantes());
    }

    @Test
    void debeVincularEstudiantesDespuesDeRegistrarArtefactos_cuandoLaAsignacionLosTrae() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var artefactos = ArtefactoDomain.crear(
                proyecto, asesor, List.of(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()));
        var idsArtefactos = artefactos.stream().map(ArtefactoDomain::getId).toList();
        var estudiantes = AgregacionEstudiantesArtefactoDomain.crear(
                asesor, EstudianteArtefactoDomain.crear(idsArtefactos, List.of(UtilUUID.generarNuevoUUID())));
        var asignacion = AsignacionArtefactosDomain.crear(artefactos, estudiantes);
        when(proyectoGradoExisteFinder.obtener(proyecto)).thenReturn(true);
        when(asesorPorIdFinder.obtener(asesor)).thenReturn(AsesorDomain.VACIO);
        when(vinculosMasRecientesProyectoGradoFinder.obtener(proyecto)).thenReturn(List.of());
        when(versionesRepositorioArtefactoPorIdsFinder.obtener(anyList())).thenReturn(List.of());
        when(repositoriosAsignadosProyectoFinder.obtener(proyecto)).thenReturn(List.of());

        // Act
        useCase.ejecutar(asignacion);

        // Assert
        var orden = inOrder(
                asignarArtefactosValidator, artefactoOutputPort, asignarEstudiantesArtefactoUseCase, logger);
        orden.verify(asignarArtefactosValidator).validar(any(), eq(true), any(), anyList(), anyList(), anyList());
        orden.verify(artefactoOutputPort).registrarTodos(anyList());
        orden.verify(asignarEstudiantesArtefactoUseCase).ejecutar(estudiantes);
        orden.verify(logger).info(eq(ArtefactoKey.LOG_ASIGNADOS), eq(proyecto), eq(2));
    }

    @Test
    void debeNoPersistirNiCerrar_cuandoElValidatorLanza() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var asignacion = asignacion(proyecto, asesor, List.of(UtilUUID.generarNuevoUUID()));
        when(proyectoGradoExisteFinder.obtener(proyecto)).thenReturn(false);
        when(asesorPorIdFinder.obtener(asesor)).thenReturn(AsesorDomain.VACIO);
        when(vinculosMasRecientesProyectoGradoFinder.obtener(proyecto)).thenReturn(List.of());
        when(versionesRepositorioArtefactoPorIdsFinder.obtener(anyList())).thenReturn(List.of());
        when(repositoriosAsignadosProyectoFinder.obtener(proyecto)).thenReturn(List.of());
        doThrow(new ProyectoGradoNoEncontradoException(proyecto)).when(asignarArtefactosValidator)
                .validar(any(), eq(false), any(), any(), anyList(), anyList());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(asignacion))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
        verify(artefactoOutputPort, never()).registrarTodos(any());
        verifyNoInteractions(asignarEstudiantesArtefactoUseCase);
        verify(logger, never()).info(any(ClaveMensaje.class), eq(proyecto), eq(1));
    }
}
