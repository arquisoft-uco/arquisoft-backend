package com.arquisoft.fichas.application.observacionevaluacion.query.usecase.impl;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionRepresentanteCriteria;
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
class ConsultarObservacionesEvaluacionRepresentanteUseCaseImplTest {

    @Mock
    private ObservacionEvaluacionQueryOutputPort observacionEvaluacionQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarObservacionesEvaluacionRepresentanteUseCaseImpl useCase;

    @Test
    void debeDevolverObservaciones_cuandoElPuertoTieneResultados() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();
        var criteria = new ObservacionEvaluacionRepresentanteCriteria(evaluacionFichaPerfil, representanteComite);
        var esperado = List.of(
                new ObservacionEvaluacionReadModel(UtilUUID.generarNuevoUUID(), evaluacionFichaPerfil,
                        "El marco teórico es insuficiente"),
                new ObservacionEvaluacionReadModel(UtilUUID.generarNuevoUUID(), evaluacionFichaPerfil,
                        "Falta delimitar el alcance"),
                new ObservacionEvaluacionReadModel(UtilUUID.generarNuevoUUID(), evaluacionFichaPerfil,
                        "Revisar la metodología"));
        when(observacionEvaluacionQueryOutputPort.consultarPorEvaluacionYRepresentanteComite(criteria))
                .thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(observacionEvaluacionQueryOutputPort, times(1)).consultarPorEvaluacionYRepresentanteComite(criteria);
        verify(logger).debug(eq(ObservacionEvaluacionKey.LOG_CONSULTANDO_REPRESENTANTE),
                eq(evaluacionFichaPerfil), eq(representanteComite));
        verify(logger).debug(eq(ObservacionEvaluacionKey.LOG_CONSULTA_REPRESENTANTE_COMPLETADA), eq(3));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }

    @Test
    void debeDevolverListaVacia_cuandoElPuertoNoTieneResultados() {
        // Arrange
        var criteria = new ObservacionEvaluacionRepresentanteCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        when(observacionEvaluacionQueryOutputPort.consultarPorEvaluacionYRepresentanteComite(criteria))
                .thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(ObservacionEvaluacionKey.LOG_CONSULTA_REPRESENTANTE_COMPLETADA), eq(0));
    }
}
