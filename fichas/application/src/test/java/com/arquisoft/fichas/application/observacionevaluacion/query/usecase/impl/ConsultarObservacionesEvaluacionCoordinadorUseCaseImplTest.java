package com.arquisoft.fichas.application.observacionevaluacion.query.usecase.impl;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionCoordinadorCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.application.observacionevaluacion.query.secondaryport.ObservacionEvaluacionQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.fichas.ObservacionEvaluacionKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarObservacionesEvaluacionCoordinadorUseCaseImplTest {

    @Mock
    private ObservacionEvaluacionQueryOutputPort observacionEvaluacionQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarObservacionesEvaluacionCoordinadorUseCaseImpl useCase;

    @Test
    void debeDevolverObservaciones_cuandoElPuertoLasEncuentra() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var criteria = new ObservacionEvaluacionCoordinadorCriteria(fichaPerfil);
        var evaluacion = UtilUUID.generarNuevoUUID();
        var esperado = List.of(
                new ObservacionEvaluacionReadModel(UtilUUID.generarNuevoUUID(), evaluacion, "Ajustar objetivos"),
                new ObservacionEvaluacionReadModel(UtilUUID.generarNuevoUUID(), evaluacion, "Precisar alcance"),
                new ObservacionEvaluacionReadModel(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                        "Revisar bibliografia"));
        when(observacionEvaluacionQueryOutputPort.consultarPorFicha(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(observacionEvaluacionQueryOutputPort, times(1)).consultarPorFicha(criteria);
        verify(logger).debug(eq(ObservacionEvaluacionKey.LOG_CONSULTANDO_COORDINADOR), eq(fichaPerfil));
        verify(logger).debug(eq(ObservacionEvaluacionKey.LOG_CONSULTA_COORDINADOR_COMPLETADA), eq(3));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }

    @Test
    void debeDevolverListaVacia_cuandoNoHayObservaciones() {
        // Arrange
        var criteria = new ObservacionEvaluacionCoordinadorCriteria(UtilUUID.generarNuevoUUID());
        when(observacionEvaluacionQueryOutputPort.consultarPorFicha(any())).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(ObservacionEvaluacionKey.LOG_CONSULTA_COORDINADOR_COMPLETADA), eq(0));
    }
}
