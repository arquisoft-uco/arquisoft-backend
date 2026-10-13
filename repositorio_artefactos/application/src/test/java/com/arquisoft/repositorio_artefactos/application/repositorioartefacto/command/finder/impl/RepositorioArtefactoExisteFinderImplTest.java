package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.RepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
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
class RepositorioArtefactoExisteFinderImplTest {

    @Mock
    private RepositorioArtefactoOutputPort outputPort;

    @InjectMocks
    private RepositorioArtefactoExisteFinderImpl finder;

    @Test
    void debeDelegarEnPuertoConLaCombinacionDelDomain() {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();
        var repositorio = RepositorioArtefactoDomain.crear("Repo", "Descripcion", administrador);
        when(outputPort.existePorNombreDescripcionYAdministrador("Repo", "Descripcion", administrador))
                .thenReturn(true);

        // Act
        var existe = finder.obtener(repositorio);

        // Assert
        assertThat(existe).isTrue();
        verify(outputPort).existePorNombreDescripcionYAdministrador("Repo", "Descripcion", administrador);
    }
}
