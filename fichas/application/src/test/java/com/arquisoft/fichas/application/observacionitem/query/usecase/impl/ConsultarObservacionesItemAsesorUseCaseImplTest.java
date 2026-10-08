package com.arquisoft.fichas.application.observacionitem.query.usecase.impl;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemCriteria;
import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.fichas.application.observacionitem.query.secondaryport.ObservacionItemQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarObservacionesItemAsesorUseCaseImplTest {

    @Mock
    private ObservacionItemQueryOutputPort observacionItemQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarObservacionesItemAsesorUseCaseImpl useCase;

    @Test
    void debeConsultarPuertoUnaSolaVezYRetornarResultado_cuandoHayCoincidencias() {
        // Arrange
        var criteria = ObservacionItemCriteria.builder().pagina(0).tamanio(10).build();
        var observaciones = List.of(
                nuevaObservacion("Pendiente", "PENDIENTE"),
                nuevaObservacion("En Progreso", "EN_PROGRESO"),
                nuevaObservacion("Cerrado", "CERRADO"));
        var resultadoEsperado = PaginatedResult.of(observaciones, 0, 10, 3L);

        when(observacionItemQueryOutputPort.consultarTodas(criteria)).thenReturn(resultadoEsperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        assertThat(resultado.getContent()).hasSize(3);
        verify(observacionItemQueryOutputPort, times(1)).consultarTodas(criteria);
        verify(logger).debug(eq(ObservacionItemKey.LOG_CONSULTANDO_ELABORADAS),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(ObservacionItemKey.LOG_CONSULTA_ELABORADAS_COMPLETADA),
                eq(resultadoEsperado.getTotalElements()));
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElPuertoNoEncuentraObservaciones() {
        // Arrange
        var criteria = ObservacionItemCriteria.builder().pagina(0).tamanio(10).build();
        var resultadoVacio = PaginatedResult.of(List.<ObservacionItemReadModel>of(), 0, 10, 0L);
        when(observacionItemQueryOutputPort.consultarTodas(any(ObservacionItemCriteria.class)))
                .thenReturn(resultadoVacio);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    private ObservacionItemReadModel nuevaObservacion(String estadoNombre, String estado) {
        return new ObservacionItemReadModel(
                UUID.randomUUID(), UUID.randomUUID(), "Observacion " + estado, estado, estadoNombre);
    }
}
