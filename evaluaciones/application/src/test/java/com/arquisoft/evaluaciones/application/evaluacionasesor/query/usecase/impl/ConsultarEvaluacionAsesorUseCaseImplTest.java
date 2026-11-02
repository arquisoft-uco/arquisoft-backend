package com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.finder.EvaluacionExisteQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionAsesorDeEvaluacionCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.EvaluacionAsesorDeEvaluacionQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport.EvaluacionAsesorDeEvaluacionQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.validator.ConsultarEvaluacionAsesorValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEvaluacionAsesorUseCaseImplTest {

    @Mock
    private EvaluacionExisteQueryFinder evaluacionExisteQueryFinder;

    @Mock
    private EvaluacionAsesorDeEvaluacionQueryFinder evaluacionAsesorDeEvaluacionQueryFinder;

    @Mock
    private ConsultarEvaluacionAsesorValidator validator;

    @Mock
    private EvaluacionAsesorDeEvaluacionQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionAsesorUseCaseImpl useCase;

    @Test
    void debeConsultar_cuandoLaValidacionPasa() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var criteria = new EvaluacionAsesorDeEvaluacionCriteria(evaluacion);
        var esperado = new EvaluacionAsesorReadModel(evaluacionAsesor,
                new EvaluacionAsesorReadModel.Asesor(UtilUUID.generarNuevoUUID(), "Ana", "ana@uco.edu.co"));
        when(evaluacionExisteQueryFinder.obtener(evaluacion)).thenReturn(true);
        when(evaluacionAsesorDeEvaluacionQueryFinder.obtener(evaluacion)).thenReturn(evaluacionAsesor);
        when(queryOutputPort.consultar(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(evaluacionExisteQueryFinder, evaluacionAsesorDeEvaluacionQueryFinder, validator,
                queryOutputPort);
        orden.verify(evaluacionExisteQueryFinder, times(1)).obtener(evaluacion);
        orden.verify(evaluacionAsesorDeEvaluacionQueryFinder, times(1)).obtener(evaluacion);
        orden.verify(validator).validar(evaluacion, true, evaluacionAsesor);
        orden.verify(queryOutputPort).consultar(criteria);
        verify(logger).debug(eq(EvaluacionAsesorKey.LOG_CONSULTA_COMPLETADA), eq(evaluacionAsesor));
    }
}
