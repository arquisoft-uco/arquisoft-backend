package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.VersionRepositorioArtefactoCriteria;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.VersionRepositorioArtefactoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarVersionesRepositorioArtefactoUseCaseImplTest {

    @Mock
    private VersionRepositorioArtefactoQueryOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarVersionesRepositorioArtefactoUseCaseImpl useCase;

    private static VersionRepositorioArtefactoReadModel version(int numero) {
        return new VersionRepositorioArtefactoReadModel(UtilUUID.generarNuevoUUID(), "Formato", numero,
                Instant.now(), "application/pdf", 2048L, "formato.pdf", null);
    }

    @Test
    void debeRetornarVersionesDelPuerto_cuandoExisten() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var versiones = List.of(version(3), version(2), version(1));
        when(outputPort.consultarPorRepositorio(repositorio)).thenReturn(versiones);

        // Act
        var resultado = useCase.ejecutar(new VersionRepositorioArtefactoCriteria(repositorio));

        // Assert
        assertThat(resultado).isSameAs(versiones);
        verify(outputPort, times(1)).consultarPorRepositorio(repositorio);
        var orden = inOrder(logger, outputPort);
        orden.verify(logger).debug(VersionRepositorioArtefactoKey.LOG_CONSULTANDO, repositorio);
        orden.verify(outputPort).consultarPorRepositorio(repositorio);
        orden.verify(logger).debug(VersionRepositorioArtefactoKey.LOG_CONSULTA_COMPLETADA, 3);
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayVersiones() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        when(outputPort.consultarPorRepositorio(repositorio)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(new VersionRepositorioArtefactoCriteria(repositorio));

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(VersionRepositorioArtefactoKey.LOG_CONSULTA_COMPLETADA, 0);
    }
}
