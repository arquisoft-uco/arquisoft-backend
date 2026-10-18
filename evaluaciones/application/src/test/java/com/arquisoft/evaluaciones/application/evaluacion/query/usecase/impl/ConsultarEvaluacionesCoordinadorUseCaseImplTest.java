package com.arquisoft.evaluaciones.application.evaluacion.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.application.evaluacion.query.secondaryport.EvaluacionQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEvaluacionesCoordinadorUseCaseImplTest {

    @Mock
    private EvaluacionQueryOutputPort evaluacionQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionesCoordinadorUseCaseImpl useCase;

    @Test
    void debeRetornarElResultadoDelPuerto_yRegistrarLasDosLineasDeDebug() {
        // Arrange
        var criteria = EvaluacionCriteria.builder().pagina(0).tamanio(10).build();
        var readModel = new EvaluacionReadModel(
                UUID.randomUUID(),
                new EvaluacionReadModel.Entregable(UUID.randomUUID(), "Robot seguidor", 1),
                new EvaluacionReadModel.Estado("PENDIENTE", "Pendiente"));
        var esperado = PaginatedResult.of(List.of(readModel), 0, 10, 1);
        when(evaluacionQueryOutputPort.consultarTodas(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(logger).debug(eq(EvaluacionKey.LOG_CONSULTANDO), eq(0), eq(10),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(EvaluacionKey.LOG_CONSULTA_COMPLETADA), eq(1L), eq(0), eq(10));
    }

    @Test
    void debeRetornarPaginaVaciaSinLanzar_cuandoNoHayCoincidencias() {
        // Arrange
        var criteria = EvaluacionCriteria.builder().build();
        PaginatedResult<EvaluacionReadModel> vacio = PaginatedResult.of(List.of(), 0, 10, 0);
        when(evaluacionQueryOutputPort.consultarTodas(criteria)).thenReturn(vacio);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.isEmpty()).isTrue();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
