package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.RepositoriosAsignadosProyectoFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.artefactos.application.artefacto.command.validator.RegistrarArtefactoValidator;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.VinculosMasRecientesProyectoGradoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.AsignarEstudiantesArtefactoUseCase;
import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoExisteFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.EstudianteArtefactoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarArtefactoUseCaseImplTest {

    @Mock
    private ProyectoGradoExisteFinder proyectoGradoExisteFinder;
    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;
    @Mock
    private VinculosMasRecientesProyectoGradoFinder vinculosMasRecientesProyectoGradoFinder;
    @Mock
    private VersionRepositorioArtefactoPorIdFinder versionRepositorioArtefactoPorIdFinder;
    @Mock
    private RepositoriosAsignadosProyectoFinder repositoriosAsignadosProyectoFinder;
    @Mock
    private RegistrarArtefactoValidator registrarArtefactoValidator;
    @Mock
    private ArtefactoOutputPort artefactoOutputPort;
    @Mock
    private AsignarEstudiantesArtefactoUseCase asignarEstudiantesArtefactoUseCase;
    @Mock
    private AppLogger logger;

    @Captor
    private ArgumentCaptor<ArtefactoEntity> entidadCaptor;

    private RegistrarArtefactoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarArtefactoUseCaseImpl(
                proyectoGradoExisteFinder, asesorPorIdFinder, vinculosMasRecientesProyectoGradoFinder,
                versionRepositorioArtefactoPorIdFinder, repositoriosAsignadosProyectoFinder,
                registrarArtefactoValidator, artefactoOutputPort, asignarEstudiantesArtefactoUseCase, logger);
    }

    private static AsignacionArtefactoDomain registro(UUID proyecto, UUID asesor, UUID version) {
        var artefacto = ArtefactoDomain.crear(proyecto, asesor, version);
        var estudiantes = AgregacionEstudiantesArtefactoDomain.crear(asesor,
                EstudianteArtefactoDomain.crear(artefacto.getId(), List.of(UtilUUID.generarNuevoUUID())));
        return AsignacionArtefactoDomain.crear(artefacto, estudiantes);
    }

    @Test
    void debeRegistrarYEncadenarEstudiantesEnOrden_yDevolverElId() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var registro = registro(proyecto, asesor, version);
        when(proyectoGradoExisteFinder.obtener(proyecto)).thenReturn(true);
        when(asesorPorIdFinder.obtener(asesor)).thenReturn(AsesorDomain.VACIO);
        when(vinculosMasRecientesProyectoGradoFinder.obtener(proyecto)).thenReturn(List.of());
        when(versionRepositorioArtefactoPorIdFinder.obtener(version))
                .thenReturn(VersionRepositorioArtefactoDomain.VACIO);
        when(repositoriosAsignadosProyectoFinder.obtener(proyecto)).thenReturn(List.of());

        // Act
        var id = useCase.ejecutar(registro);

        // Assert
        assertThat(id).isEqualTo(registro.getArtefacto().getId());
        var orden = inOrder(proyectoGradoExisteFinder, asesorPorIdFinder, vinculosMasRecientesProyectoGradoFinder,
                versionRepositorioArtefactoPorIdFinder, repositoriosAsignadosProyectoFinder, logger,
                registrarArtefactoValidator, artefactoOutputPort, asignarEstudiantesArtefactoUseCase);
        orden.verify(proyectoGradoExisteFinder, times(1)).obtener(proyecto);
        orden.verify(asesorPorIdFinder, times(1)).obtener(asesor);
        orden.verify(vinculosMasRecientesProyectoGradoFinder, times(1)).obtener(proyecto);
        orden.verify(versionRepositorioArtefactoPorIdFinder, times(1)).obtener(version);
        orden.verify(repositoriosAsignadosProyectoFinder, times(1)).obtener(proyecto);
        orden.verify(logger).debug(ArtefactoKey.LOG_VERIFICACION_REGISTRAR, true, false, 0, false, 0);
        orden.verify(registrarArtefactoValidator).validar(
                registro, true, AsesorDomain.VACIO, List.of(), VersionRepositorioArtefactoDomain.VACIO, List.of());
        orden.verify(artefactoOutputPort).registrar(entidadCaptor.capture());
        orden.verify(asignarEstudiantesArtefactoUseCase).ejecutar(registro.getEstudiantes());
        assertThat(entidadCaptor.getValue().id()).isEqualTo(id);
    }

    @Test
    void debeNoRegistrarNiEncadenar_cuandoElValidatorLanza() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var registro = registro(proyecto, asesor, UtilUUID.generarNuevoUUID());
        when(proyectoGradoExisteFinder.obtener(proyecto)).thenReturn(false);
        when(asesorPorIdFinder.obtener(asesor)).thenReturn(AsesorDomain.VACIO);
        when(vinculosMasRecientesProyectoGradoFinder.obtener(proyecto)).thenReturn(List.of());
        when(versionRepositorioArtefactoPorIdFinder.obtener(any()))
                .thenReturn(VersionRepositorioArtefactoDomain.VACIO);
        when(repositoriosAsignadosProyectoFinder.obtener(proyecto)).thenReturn(List.of());
        doThrow(new ProyectoGradoNoEncontradoException(proyecto)).when(registrarArtefactoValidator)
                .validar(any(), eq(false), any(), any(), any(), anyList());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(registro))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
        verify(artefactoOutputPort, never()).registrar(any());
        verify(asignarEstudiantesArtefactoUseCase, never()).ejecutar(any());
    }
}
