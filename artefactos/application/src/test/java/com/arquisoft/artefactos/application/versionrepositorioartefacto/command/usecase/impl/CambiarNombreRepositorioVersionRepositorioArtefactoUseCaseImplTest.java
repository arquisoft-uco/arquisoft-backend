package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.CambioNombreRepositorioDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CambiarNombreRepositorioVersionRepositorioArtefactoUseCaseImplTest {

    @Mock
    private VersionRepositorioArtefactoOutputPort outputPort;

    @InjectMocks
    private CambiarNombreRepositorioVersionRepositorioArtefactoUseCaseImpl useCase;

    @Test
    void debeLlamarAlPuerto_cuandoSeEjecuta() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var cambio = CambioNombreRepositorioDomain.crear(repositorio, "Repositorio nuevo");

        // Act
        useCase.ejecutar(cambio);

        // Assert
        verify(outputPort).actualizarNombreRepositorio(repositorio, "Repositorio nuevo");
    }
}
