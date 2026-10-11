package com.arquisoft.fichas.application.observacionitem.query.usecase.impl;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemCriteria;
import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemEstudianteCriteria;
import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.fichas.application.observacionitem.query.secondaryport.ObservacionItemQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarObservacionesItemEstudianteUseCaseImplTest {

    @Mock
    private ObservacionItemQueryOutputPort observacionItemQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarObservacionesItemEstudianteUseCaseImpl useCase;

    @Test
    void debeConsultarPuertoUnaSolaVezYRetornarResultado_cuandoHayCoincidencias() {
        // Arrange
        var criteria = ObservacionItemEstudianteCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estudianteId", FiltroOperador.ES, UUID.randomUUID().toString()))
                .build();
        var observacion = new ObservacionItemReadModel(
                UUID.randomUUID(), UUID.randomUUID(), "Falta precisar el alcance", "PENDIENTE", "Pendiente");
        var resultadoEsperado = PaginatedResult.of(List.of(observacion), 0, 10, 1L);
        when(observacionItemQueryOutputPort.consultarTodasEstudiante(criteria)).thenReturn(resultadoEsperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(observacionItemQueryOutputPort, times(1)).consultarTodasEstudiante(criteria);
        verify(observacionItemQueryOutputPort, never()).consultarTodas(any(ObservacionItemCriteria.class));
        verify(logger).debug(eq(ObservacionItemKey.LOG_CONSULTANDO_ESTUDIANTE),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(ObservacionItemKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA),
                eq(resultadoEsperado.getTotalElements()));
    }

    @Test
    void debeRetornarPaginaVacia_cuandoEstudianteSinObservaciones() {
        // Arrange
        var criteria = ObservacionItemEstudianteCriteria.builder().pagina(0).tamanio(10).build();
        var resultadoVacio = PaginatedResult.of(List.<ObservacionItemReadModel>of(), 0, 10, 0L);
        when(observacionItemQueryOutputPort.consultarTodasEstudiante(any(ObservacionItemEstudianteCriteria.class)))
                .thenReturn(resultadoVacio);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
        verify(logger).debug(eq(ObservacionItemKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA), eq(0L));
    }
}
