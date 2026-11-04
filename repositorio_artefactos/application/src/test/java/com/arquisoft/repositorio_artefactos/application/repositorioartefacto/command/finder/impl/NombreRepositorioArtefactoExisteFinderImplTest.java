package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.RepositorioArtefactoOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NombreRepositorioArtefactoExisteFinderImplTest {

    @Mock
    private RepositorioArtefactoOutputPort outputPort;

    @InjectMocks
    private NombreRepositorioArtefactoExisteFinderImpl finder;

    @Test
    void debeDelegarEnPuertoConElNombre_cuandoSeConsulta() {
        // Arrange
        when(outputPort.existePorNombreRepositorio("Repo")).thenReturn(true);

        // Act
        var existe = finder.obtener("Repo");

        // Assert
        assertThat(existe).isTrue();
        verify(outputPort).existePorNombreRepositorio("Repo");
    }
}
