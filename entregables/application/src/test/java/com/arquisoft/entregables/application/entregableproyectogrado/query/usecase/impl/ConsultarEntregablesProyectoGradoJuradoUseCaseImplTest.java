package com.arquisoft.entregables.application.entregableproyectogrado.query.usecase.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;
import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.EntregableProyectoGradoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEntregablesProyectoGradoJuradoUseCaseImplTest {

    @Mock
    private EntregableProyectoGradoQueryOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEntregablesProyectoGradoJuradoUseCaseImpl useCase;

    @Test
    void debeConsultarElPuertoUnaVez_yRetornarSuResultado() {
        // Arrange
        var criteria = EntregableProyectoGradoCriteria.builder().pagina(2).tamanio(5).build();
        var esperado = PaginatedResult.<EntregableProyectoGradoReadModel>of(List.of(), 2, 5, 11L);
        when(outputPort.consultar(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(logger, outputPort);
        orden.verify(logger).debug(eq(EntregableProyectoGradoKey.LOG_CONSULTANDO),
                eq(2), eq(5), eq(false), eq(false));
        orden.verify(outputPort, times(1)).consultar(criteria);
        orden.verify(logger).debug(eq(EntregableProyectoGradoKey.LOG_CONSULTA_COMPLETADA),
                eq(11L), eq(2), eq(5));
    }
}
