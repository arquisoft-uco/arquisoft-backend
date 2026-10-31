package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.validator.ReactivarArtefactoValidator;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.VinculosMasRecientesProyectoGradoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudiantesVigentesVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.AsignarEstudiantesArtefactoUseCase;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.DepurarEstudiantesArtefactoUseCase;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.EstudiantesVigentesProyectoGradoFinder;
import com.arquisoft.artefactos.domain.artefacto.ReactivacionArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.DepuracionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudiantesSuperanProyectoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.CupoEstudiantesArtefacto;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReactivarArtefactoUseCaseImplTest {

    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;
    @Mock
    private VinculosMasRecientesProyectoGradoFinder vinculosMasRecientesProyectoGradoFinder;
    @Mock
    private EstudiantesVigentesProyectoGradoFinder estudiantesVigentesProyectoGradoFinder;
    @Mock
    private EstudiantesVigentesVinculadosArtefactoFinder estudiantesVigentesVinculadosArtefactoFinder;
    @Mock
    private ReactivarArtefactoValidator reactivarArtefactoValidator;
    @Mock
    private ArtefactoOutputPort artefactoOutputPort;
    @Mock
    private DepurarEstudiantesArtefactoUseCase depurarEstudiantesArtefactoUseCase;
    @Mock
    private AsignarEstudiantesArtefactoUseCase asignarEstudiantesArtefactoUseCase;
    @Mock
    private AppLogger logger;

    @Captor
    private ArgumentCaptor<DepuracionEstudiantesArtefactoDomain> depuracionCaptor;
    @Captor
    private ArgumentCaptor<AgregacionEstudiantesArtefactoDomain> agregacionCaptor;

    private ReactivarArtefactoUseCaseImpl useCase;

    private UUID artefacto;
    private UUID proyecto;
    private UUID asesor;

    @BeforeEach
    void setUp() {
        useCase = new ReactivarArtefactoUseCaseImpl(
                asesorPorIdFinder, vinculosMasRecientesProyectoGradoFinder, estudiantesVigentesProyectoGradoFinder,
                estudiantesVigentesVinculadosArtefactoFinder, reactivarArtefactoValidator, artefactoOutputPort,
                depurarEstudiantesArtefactoUseCase, asignarEstudiantesArtefactoUseCase, logger);
        artefacto = UtilUUID.generarNuevoUUID();
        proyecto = UtilUUID.generarNuevoUUID();
        asesor = UtilUUID.generarNuevoUUID();
        when(asesorPorIdFinder.obtener(asesor)).thenReturn(AsesorDomain.VACIO);
        when(vinculosMasRecientesProyectoGradoFinder.obtener(proyecto)).thenReturn(List.of());
    }

    @Test
    void debeReactivarDepurarYVincularSoloNuevos_cuandoHayEstudiantesSinVincular() {
        // Arrange
        var conservado = UtilUUID.generarNuevoUUID();
        var nuevo = UtilUUID.generarNuevoUUID();
        var reactivacion = ReactivacionArtefactoDomain.crear(artefacto, proyecto, asesor, List.of(conservado, nuevo));
        when(estudiantesVigentesProyectoGradoFinder.obtener(proyecto)).thenReturn(List.of(conservado, nuevo));
        when(estudiantesVigentesVinculadosArtefactoFinder.obtener(artefacto)).thenReturn(List.of(conservado));

        // Act
        useCase.ejecutar(reactivacion);

        // Assert
        var orden = inOrder(asesorPorIdFinder, vinculosMasRecientesProyectoGradoFinder,
                estudiantesVigentesProyectoGradoFinder, estudiantesVigentesVinculadosArtefactoFinder, logger,
                reactivarArtefactoValidator, artefactoOutputPort, depurarEstudiantesArtefactoUseCase,
                asignarEstudiantesArtefactoUseCase);
        orden.verify(asesorPorIdFinder, times(1)).obtener(asesor);
        orden.verify(vinculosMasRecientesProyectoGradoFinder, times(1)).obtener(proyecto);
        orden.verify(estudiantesVigentesProyectoGradoFinder, times(1)).obtener(proyecto);
        orden.verify(estudiantesVigentesVinculadosArtefactoFinder, times(1)).obtener(artefacto);
        orden.verify(logger).debug(ArtefactoKey.LOG_VERIFICACION_REACTIVAR, false, 0, 2, 1);
        orden.verify(reactivarArtefactoValidator).validar(reactivacion, AsesorDomain.VACIO, List.of(),
                new CupoEstudiantesArtefacto(List.of(conservado, nuevo), List.of(conservado),
                        List.of(conservado, nuevo)));
        orden.verify(artefactoOutputPort).reactivar(artefacto, asesor);
        orden.verify(depurarEstudiantesArtefactoUseCase).ejecutar(depuracionCaptor.capture());
        orden.verify(asignarEstudiantesArtefactoUseCase).ejecutar(agregacionCaptor.capture());
        assertThat(depuracionCaptor.getValue().getArtefacto()).isEqualTo(artefacto);
        assertThat(depuracionCaptor.getValue().getConservar()).containsExactly(conservado, nuevo);
        assertThat(agregacionCaptor.getValue().getArtefacto()).isEqualTo(artefacto);
        assertThat(agregacionCaptor.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(agregacionCaptor.getValue().getEstudiantes()).containsExactly(nuevo);
    }

    @Test
    void debeConservarSoloLosEnviados_cuandoSeEnviaUnoDeTresPrevios() {
        // Arrange
        var enviado = UtilUUID.generarNuevoUUID();
        var previoUno = UtilUUID.generarNuevoUUID();
        var previoDos = UtilUUID.generarNuevoUUID();
        var reactivacion = ReactivacionArtefactoDomain.crear(artefacto, proyecto, asesor, List.of(enviado));
        when(estudiantesVigentesProyectoGradoFinder.obtener(proyecto))
                .thenReturn(List.of(enviado, previoUno, previoDos));
        when(estudiantesVigentesVinculadosArtefactoFinder.obtener(artefacto))
                .thenReturn(List.of(enviado, previoUno, previoDos));

        // Act
        useCase.ejecutar(reactivacion);

        // Assert
        verify(artefactoOutputPort).reactivar(artefacto, asesor);
        verify(depurarEstudiantesArtefactoUseCase).ejecutar(depuracionCaptor.capture());
        assertThat(depuracionCaptor.getValue().getConservar()).containsExactly(enviado);
        verify(asignarEstudiantesArtefactoUseCase, never()).ejecutar(any());
    }

    @Test
    void debeNoEscribirNada_cuandoElValidatorLanza() {
        // Arrange
        var nuevo = UtilUUID.generarNuevoUUID();
        var reactivacion = ReactivacionArtefactoDomain.crear(artefacto, proyecto, asesor, List.of(nuevo));
        when(estudiantesVigentesProyectoGradoFinder.obtener(proyecto)).thenReturn(List.of());
        when(estudiantesVigentesVinculadosArtefactoFinder.obtener(artefacto)).thenReturn(List.of());
        doThrow(new EstudiantesSuperanProyectoException(0, 1)).when(reactivarArtefactoValidator)
                .validar(any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(reactivacion))
                .isInstanceOf(EstudiantesSuperanProyectoException.class);
        verify(artefactoOutputPort, never()).reactivar(any(), any());
        verify(depurarEstudiantesArtefactoUseCase, never()).ejecutar(any());
        verify(asignarEstudiantesArtefactoUseCase, never()).ejecutar(any());
    }
}
