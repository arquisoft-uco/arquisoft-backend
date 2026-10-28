package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.VersionRepositorioArtefactoAsesorCriteria;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.RepositorioDeVersionReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoAsesorReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.VersionRepositorioArtefactoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
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
class ConsultarVersionesRepositorioArtefactoAsesorUseCaseImplTest {

    @Mock
    private VersionRepositorioArtefactoQueryOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarVersionesRepositorioArtefactoAsesorUseCaseImpl useCase;

    @Test
    void debeDelegarAlPuertoYRegistrarLosDosDebug_cuandoHayCriteria() {
        // Arrange
        var criteria = VersionRepositorioArtefactoAsesorCriteria.builder().pagina(0).tamanio(10).build();
        var version = new VersionRepositorioArtefactoAsesorReadModel(
                UtilUUID.generarNuevoUUID(), "Formato A", 1, Instant.parse("2026-10-20T10:00:00Z"),
                "application/pdf", 2048L, "formato.pdf", null,
                new RepositorioDeVersionReadModel(UtilUUID.generarNuevoUUID(), "Repo", "Descripcion"));
        var pagina = PaginatedResult.of(List.of(version), 0, 10, 1L);
        when(outputPort.consultarParaAsesor(criteria)).thenReturn(pagina);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(pagina);
        verify(outputPort, times(1)).consultarParaAsesor(criteria);
        var orden = inOrder(logger, outputPort);
        orden.verify(logger).debug(VersionRepositorioArtefactoKey.LOG_CONSULTANDO_ASESOR, 0, 10, false, false);
        orden.verify(outputPort).consultarParaAsesor(criteria);
        orden.verify(logger).debug(VersionRepositorioArtefactoKey.LOG_CONSULTA_ASESOR_COMPLETADA, 1L, 0, 10);
    }
}
