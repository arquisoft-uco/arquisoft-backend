package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.ArchivoVersionPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.EliminacionVersionRepositorioArtefactoResult;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.ArchivoVersionDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.EliminacionVersionRepositorioArtefactoDomain;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarVersionRepositorioArtefactoUseCaseImplTest {

    private static final String URI_ARCHIVO = "versiones/abc";

    @Mock
    private ArchivoVersionPorIdFinder archivoVersionPorIdFinder;
    @Mock
    private AlmacenamientoVersionOutputPort almacenamientoVersionOutputPort;
    @Mock
    private VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;
    @Mock
    private AppLogger logger;

    private EliminarVersionRepositorioArtefactoUseCaseImpl useCase;

    private UUID versionId;
    private EliminacionVersionRepositorioArtefactoDomain eliminacion;

    @BeforeEach
    void setUp() {
        useCase = new EliminarVersionRepositorioArtefactoUseCaseImpl(
                archivoVersionPorIdFinder, almacenamientoVersionOutputPort,
                versionRepositorioArtefactoOutputPort, logger);
        versionId = UtilUUID.generarNuevoUUID();
        eliminacion = EliminacionVersionRepositorioArtefactoDomain.crear(versionId);
    }

    @Test
    void debeBorrarArchivoYLuegoFila_cuandoLaVersionExiste() {
        // Arrange
        when(archivoVersionPorIdFinder.obtener(versionId))
                .thenReturn(ArchivoVersionDomain.reconstruir(URI_ARCHIVO, "a.pdf"));

        // Act
        var resultado = useCase.ejecutar(eliminacion);

        // Assert
        var orden = inOrder(archivoVersionPorIdFinder, logger, almacenamientoVersionOutputPort,
                versionRepositorioArtefactoOutputPort);
        orden.verify(archivoVersionPorIdFinder).obtener(versionId);
        orden.verify(logger).debug(VersionRepositorioArtefactoKey.LOG_VERIFICACION_ELIMINAR, versionId, true);
        orden.verify(almacenamientoVersionOutputPort).eliminarArchivo(URI_ARCHIVO);
        orden.verify(versionRepositorioArtefactoOutputPort).eliminar(versionId);
        assertThat(resultado).isEqualTo(new EliminacionVersionRepositorioArtefactoResult.Eliminada(versionId));
    }

    @Test
    void debeDevolverInexistente_cuandoNoHayFila() {
        // Arrange
        when(archivoVersionPorIdFinder.obtener(versionId)).thenReturn(ArchivoVersionDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(eliminacion);

        // Assert
        assertThat(resultado).isEqualTo(new EliminacionVersionRepositorioArtefactoResult.Inexistente(versionId));
        verify(logger).debug(VersionRepositorioArtefactoKey.LOG_VERIFICACION_ELIMINAR, versionId, false);
        verifyNoInteractions(almacenamientoVersionOutputPort);
        verify(versionRepositorioArtefactoOutputPort, never()).eliminar(any());
    }

    @Test
    void debePropagarYNoBorrarLaFila_cuandoMinioFalla() {
        // Arrange
        when(archivoVersionPorIdFinder.obtener(versionId))
                .thenReturn(ArchivoVersionDomain.reconstruir(URI_ARCHIVO, "a.pdf"));
        doThrow(new InfrastructureException("minio caido", "MINIO_NO_DISPONIBLE", new IllegalStateException("503")))
                .when(almacenamientoVersionOutputPort).eliminarArchivo(URI_ARCHIVO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(eliminacion)).isInstanceOf(InfrastructureException.class);
        verify(versionRepositorioArtefactoOutputPort, never()).eliminar(any());
    }
}
