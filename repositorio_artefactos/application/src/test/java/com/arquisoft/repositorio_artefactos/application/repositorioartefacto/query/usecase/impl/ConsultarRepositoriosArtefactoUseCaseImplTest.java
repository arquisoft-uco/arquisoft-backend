package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.query.readmodel.AdministradorReadModel;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.criteria.RepositorioArtefactoCriteria;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.readmodel.RepositorioArtefactoReadModel;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.secondaryport.RepositorioArtefactoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.RepositorioArtefactoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarRepositoriosArtefactoUseCaseImplTest {

    @Mock
    private RepositorioArtefactoQueryOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarRepositoriosArtefactoUseCaseImpl useCase;

    @Test
    void debeRetornarPaginaDelPuertoYRegistrarLosDosDebug_cuandoConsulta() {
        // Arrange
        var criteria = RepositorioArtefactoCriteria.builder().pagina(0).tamanio(10).build();
        var administrador = new AdministradorReadModel(
                UtilUUID.generarNuevoUUID(), "123", "Ana Perez", "ana@uco.edu.co");
        var repositorio = new RepositorioArtefactoReadModel(
                UtilUUID.generarNuevoUUID(), "Repo", "Descripcion", administrador);
        var pagina = PaginatedResult.of(List.of(repositorio), 0, 10, 1L);
        when(outputPort.consultarTodos(criteria)).thenReturn(pagina);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(pagina);
        verify(outputPort, times(1)).consultarTodos(criteria);
        var orden = inOrder(logger, outputPort);
        orden.verify(logger).debug(RepositorioArtefactoKey.LOG_CONSULTANDO, 0, 10, false, false);
        orden.verify(outputPort).consultarTodos(criteria);
        orden.verify(logger).debug(RepositorioArtefactoKey.LOG_CONSULTA_COMPLETADA, 1L, 0, 10);
    }
}
