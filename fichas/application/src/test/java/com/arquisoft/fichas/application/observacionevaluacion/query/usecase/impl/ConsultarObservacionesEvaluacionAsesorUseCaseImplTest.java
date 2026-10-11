package com.arquisoft.fichas.application.observacionevaluacion.query.usecase.impl;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionAsesorCriteria;
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
class ConsultarObservacionesEvaluacionAsesorUseCaseImplTest {

    @Mock
    private ObservacionEvaluacionQueryOutputPort observacionEvaluacionQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarObservacionesEvaluacionAsesorUseCaseImpl useCase;

    @Test
    void debeDevolverObservaciones_cuandoElPuertoTieneResultados() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var criteria = new ObservacionEvaluacionAsesorCriteria(evaluacionFichaPerfil, asesorFicha);
        var esperado = List.of(
                new ObservacionEvaluacionReadModel(UtilUUID.generarNuevoUUID(), evaluacionFichaPerfil,
                        "El marco teórico es insuficiente"),
                new ObservacionEvaluacionReadModel(UtilUUID.generarNuevoUUID(), evaluacionFichaPerfil,
                        "Falta delimitar el alcance"),
                new ObservacionEvaluacionReadModel(UtilUUID.generarNuevoUUID(), evaluacionFichaPerfil,
                        "Revisar la metodología"));
        when(observacionEvaluacionQueryOutputPort.consultarPorEvaluacionYAsesorFicha(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(observacionEvaluacionQueryOutputPort, times(1)).consultarPorEvaluacionYAsesorFicha(criteria);
        verify(logger).debug(eq(ObservacionEvaluacionKey.LOG_CONSULTANDO_ASESOR),
                eq(evaluacionFichaPerfil), eq(asesorFicha));
        verify(logger).debug(eq(ObservacionEvaluacionKey.LOG_CONSULTA_ASESOR_COMPLETADA), eq(3));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }

    @Test
    void debeDevolverListaVacia_cuandoElPuertoNoTieneResultados() {
        // Arrange
        var criteria = new ObservacionEvaluacionAsesorCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        when(observacionEvaluacionQueryOutputPort.consultarPorEvaluacionYAsesorFicha(criteria)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
        verify(observacionEvaluacionQueryOutputPort, times(1)).consultarPorEvaluacionYAsesorFicha(criteria);
        verify(logger).debug(eq(ObservacionEvaluacionKey.LOG_CONSULTA_ASESOR_COMPLETADA), eq(0));
    }
}
