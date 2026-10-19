package com.arquisoft.evaluaciones.application.evaluacion.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.application.evaluacion.query.secondaryport.EvaluacionAsesorQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEvaluacionesAsesorUseCaseImplTest {

    @Mock
    private EvaluacionAsesorQueryOutputPort evaluacionAsesorQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionesAsesorUseCaseImpl useCase;

    @Test
    void debeRetornarElResultadoDelPuertoConUnaSolaConsulta_yRegistrarLasDosLineasDeDebug() {
        // Arrange
        var criteria = EvaluacionAsesorCriteria.builder().pagina(0).tamanio(10).build();
        var readModel = new EvaluacionReadModel(
                UtilUUID.generarNuevoUUID(),
                new EvaluacionReadModel.Entregable(UtilUUID.generarNuevoUUID(), "Robot seguidor", 1),
                new EvaluacionReadModel.Estado("PENDIENTE", "Pendiente"));
        var esperado = PaginatedResult.of(List.of(readModel), 0, 10, 1);
        when(evaluacionAsesorQueryOutputPort.consultarPorAsesor(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(evaluacionAsesorQueryOutputPort, times(1)).consultarPorAsesor(criteria);
        verify(logger).debug(eq(EvaluacionKey.LOG_CONSULTANDO), eq(0), eq(10),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(EvaluacionKey.LOG_CONSULTA_COMPLETADA), eq(1L), eq(0), eq(10));
    }
}
