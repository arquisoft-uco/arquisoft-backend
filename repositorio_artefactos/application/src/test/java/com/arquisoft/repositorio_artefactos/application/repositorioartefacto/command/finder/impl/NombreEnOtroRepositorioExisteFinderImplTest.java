package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.RepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.ModificacionRepositorioArtefactoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NombreEnOtroRepositorioExisteFinderImplTest {

    @Mock
    private RepositorioArtefactoOutputPort outputPort;

    @InjectMocks
    private NombreEnOtroRepositorioExisteFinderImpl finder;

    @Test
    void debeDelegarEnPuertoConNombreEId_cuandoSeConsulta() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var modificacion = ModificacionRepositorioArtefactoDomain.crear(
                repositorio, "Repo", "Descripcion", UtilUUID.generarNuevoUUID());
        when(outputPort.existeOtroConNombreRepositorio("Repo", repositorio)).thenReturn(true);

        // Act
        var existe = finder.obtener(modificacion);

        // Assert
        assertThat(existe).isTrue();
        verify(outputPort).existeOtroConNombreRepositorio("Repo", repositorio);
    }
}
