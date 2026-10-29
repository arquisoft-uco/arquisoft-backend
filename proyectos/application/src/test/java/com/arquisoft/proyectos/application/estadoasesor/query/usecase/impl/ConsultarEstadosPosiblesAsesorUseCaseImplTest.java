package com.arquisoft.proyectos.application.estadoasesor.query.usecase.impl;

import com.arquisoft.proyectos.application.estadoasesor.query.criteria.EstadoAsesorCriteria;
import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.proyectos.application.estadoasesor.query.secondaryport.EstadoAsesorQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstadoAsesorKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
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
class ConsultarEstadosPosiblesAsesorUseCaseImplTest {

    @Mock
    private EstadoAsesorQueryOutputPort estadoAsesorQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosPosiblesAsesorUseCaseImpl useCase;

    @Test
    void debeRetornarLaPaginaDelPuertoYLoguearEntradaYCierre_cuandoExisten() {
        // Arrange
        var criteria = EstadoAsesorCriteria.builder().pagina(0).tamanio(10).build();
        var estado = new EstadoAsesorReadModel("ASIGNADO", "Asignado", "Asesor asignado");
        var esperado = PaginatedResult.of(List.of(estado), 0, 10, 1L);
        when(estadoAsesorQueryOutputPort.consultarTodos(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(estadoAsesorQueryOutputPort, times(1)).consultarTodos(criteria);
        var orden = inOrder(logger, estadoAsesorQueryOutputPort);
        orden.verify(logger).debug(eq(EstadoAsesorKey.LOG_CONSULTANDO), eq(0), eq(10),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        orden.verify(estadoAsesorQueryOutputPort).consultarTodos(criteria);
        orden.verify(logger).debug(eq(EstadoAsesorKey.LOG_CONSULTA_COMPLETADA), eq(1L), eq(0), eq(10));
    }
}
