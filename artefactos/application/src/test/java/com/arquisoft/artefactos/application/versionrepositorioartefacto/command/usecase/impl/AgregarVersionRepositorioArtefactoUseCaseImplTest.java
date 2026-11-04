package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoExisteFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.result.AgregacionVersionRepositorioArtefactoResult;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarVersionRepositorioArtefactoUseCaseImplTest {

    @Mock
    private VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;
    @Mock
    private VersionRepositorioArtefactoExisteFinder versionRepositorioArtefactoExisteFinder;
    @Mock
    private AppLogger logger;

    private AgregarVersionRepositorioArtefactoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarVersionRepositorioArtefactoUseCaseImpl(
                versionRepositorioArtefactoOutputPort, versionRepositorioArtefactoExisteFinder, logger);
    }

    private VersionRepositorioArtefactoDomain version(UUID id, UUID repositorio) {
        return VersionRepositorioArtefactoDomain.crear(id, "Plantilla", 2, repositorio, "Repositorio",
                new DatosArchivo("plantilla.pdf", "application/pdf", 2048L));
    }

    @Test
    void debeGuardarYRetornarAgregada_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        when(versionRepositorioArtefactoExisteFinder.obtener(id)).thenReturn(false);

        // Act
        var resultado = useCase.ejecutar(version(id, repositorio));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionVersionRepositorioArtefactoResult.Agregada.class,
                agregada -> assertThat(agregada.versionRepositorioArtefacto()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(VersionRepositorioArtefactoEntity.class);
        verify(versionRepositorioArtefactoExisteFinder, times(1)).obtener(any());
        verify(versionRepositorioArtefactoOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new VersionRepositorioArtefactoEntity(
                id, "Plantilla", 2, repositorio, "Repositorio", "plantilla.pdf", "application/pdf", 2048L));
    }

    @Test
    void debeRetornarDuplicadaSinGuardar_cuandoYaExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(versionRepositorioArtefactoExisteFinder.obtener(id)).thenReturn(true);

        // Act
        var resultado = useCase.ejecutar(version(id, UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionVersionRepositorioArtefactoResult.Duplicada.class,
                duplicada -> assertThat(duplicada.versionRepositorioArtefacto()).isEqualTo(id));
        verify(versionRepositorioArtefactoOutputPort, never()).guardar(any());
    }
}
