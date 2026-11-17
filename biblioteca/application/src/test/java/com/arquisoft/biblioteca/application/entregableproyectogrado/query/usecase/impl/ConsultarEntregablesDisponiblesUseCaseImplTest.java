package com.arquisoft.biblioteca.application.entregableproyectogrado.query.usecase.impl;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.criteria.EntregableDisponibleCriteria;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.readmodel.EntregableDisponibleReadModel;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.secondaryport.EntregableDisponibleQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.EntregableProyectoGradoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEntregablesDisponiblesUseCaseImplTest {

    @Mock
    private EntregableDisponibleQueryOutputPort entregableDisponibleQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEntregablesDisponiblesUseCaseImpl useCase;

    @Test
    void debeDelegarEnElPuertoUnaVezYRetornarLaPagina_cuandoHayCriteria() {
        // Arrange
        var criteria = EntregableDisponibleCriteria.builder().pagina(0).tamanio(10).build();
        var esperado = PaginatedResult.<EntregableDisponibleReadModel>of(List.of(), 0, 10, 0L);
        when(entregableDisponibleQueryOutputPort.consultar(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(entregableDisponibleQueryOutputPort, times(1)).consultar(criteria);
        verify(logger).debug(eq(EntregableProyectoGradoKey.LOG_CONSULTANDO_DISPONIBLES),
                eq(criteria.getPagina()), eq(criteria.getTamanio()), eq(criteria.tieneFiltros()));
        verify(logger).debug(eq(EntregableProyectoGradoKey.LOG_CONSULTA_DISPONIBLES_COMPLETADA),
                eq(esperado.getTotalElements()), eq(criteria.getPagina()), eq(criteria.getTamanio()));
    }
}
