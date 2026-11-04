package com.arquisoft.proyectos.application.proyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.estadoproyecto.query.readmodel.EstadoProyectoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.secondaryport.ProyectoGradoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarProyectosGradoCoordinadorUseCaseImplTest {

    @Mock
    private ProyectoGradoQueryOutputPort proyectoGradoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarProyectosGradoCoordinadorUseCaseImpl useCase;

    @Test
    void debeDevolverPaginaYLoguear_cuandoPuertoResponde() {
        // Arrange
        var criteria = ProyectoGradoCriteria.builder().pagina(0).tamanio(10).build();
        var proyecto = new ProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Sistema de gestion", new EstadoProyectoReadModel("EN_PROCESO", "En proceso"));
        var esperado = PaginatedResult.of(List.of(proyecto), 0, 10, 1L);
        when(proyectoGradoQueryOutputPort.consultarTodas(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        assertThat(resultado.getContent()).containsExactly(proyecto);
        verify(proyectoGradoQueryOutputPort, times(1)).consultarTodas(criteria);
        var orden = inOrder(logger, proyectoGradoQueryOutputPort);
        orden.verify(logger).debug(eq(ProyectoGradoKey.LOG_CONSULTANDO), eq(0), eq(10),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        orden.verify(proyectoGradoQueryOutputPort).consultarTodas(criteria);
        orden.verify(logger).debug(eq(ProyectoGradoKey.LOG_CONSULTA_COMPLETADA), eq(1L), eq(0), eq(10));
    }
}
