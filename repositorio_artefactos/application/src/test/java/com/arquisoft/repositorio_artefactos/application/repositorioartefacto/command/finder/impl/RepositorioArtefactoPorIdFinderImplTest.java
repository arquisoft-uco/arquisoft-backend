package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.RepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.entity.RepositorioArtefactoEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositorioArtefactoPorIdFinderImplTest {

    @Mock
    private RepositorioArtefactoOutputPort outputPort;

    @InjectMocks
    private RepositorioArtefactoPorIdFinderImpl finder;

    @Test
    void debeRetornarDomain_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorId(id))
                .thenReturn(Optional.of(new RepositorioArtefactoEntity(id, "Repo", "Desc", administrador)));

        // Act
        var repositorio = finder.obtener(id);

        // Assert
        assertThat(repositorio.esVacio()).isFalse();
        assertThat(repositorio.getId()).isEqualTo(id);
        assertThat(repositorio.getNombreRepositorio()).isEqualTo("Repo");
        assertThat(repositorio.getAdministrador()).isEqualTo(administrador);
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThat(finder.obtener(id).esVacio()).isTrue();
    }
}
