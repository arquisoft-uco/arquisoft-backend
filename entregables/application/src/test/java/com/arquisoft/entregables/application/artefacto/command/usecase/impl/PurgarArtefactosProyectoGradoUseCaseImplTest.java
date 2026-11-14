package com.arquisoft.entregables.application.artefacto.command.usecase.impl;

import com.arquisoft.entregables.application.artefacto.command.finder.ClavesArtefactosProyectoGradoFinder;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.AlmacenamientoArtefactoOutputPort;
import com.arquisoft.entregables.domain.artefacto.PurgaArtefactosProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurgarArtefactosProyectoGradoUseCaseImplTest {

    @Mock
    private ClavesArtefactosProyectoGradoFinder clavesArtefactosProyectoGradoFinder;

    @Mock
    private AlmacenamientoArtefactoOutputPort almacenamientoArtefactoOutputPort;

    @InjectMocks
    private PurgarArtefactosProyectoGradoUseCaseImpl useCase;

    @Test
    void debeEliminarCadaCopia_cuandoElProyectoTieneArtefactos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(clavesArtefactosProyectoGradoFinder.obtener(proyecto)).thenReturn(List.of("clave-a", "clave-b", "clave-c"));

        // Act
        var resultado = useCase.ejecutar(PurgaArtefactosProyectoGradoDomain.crear(proyecto));

        // Assert
        verify(almacenamientoArtefactoOutputPort).eliminar("clave-a");
        verify(almacenamientoArtefactoOutputPort).eliminar("clave-b");
        verify(almacenamientoArtefactoOutputPort).eliminar("clave-c");
        assertThat(resultado.proyectoGrado()).isEqualTo(proyecto);
        assertThat(resultado.eliminadas()).isEqualTo(3);
    }

    @Test
    void noDebeEliminarNada_cuandoElProyectoNoTieneCopias() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(clavesArtefactosProyectoGradoFinder.obtener(proyecto)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(PurgaArtefactosProyectoGradoDomain.crear(proyecto));

        // Assert
        verify(almacenamientoArtefactoOutputPort, never()).eliminar(anyString());
        assertThat(resultado.eliminadas()).isZero();
    }
}
