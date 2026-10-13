package com.arquisoft.fichas.application.observacionevaluacion.command.finder.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import com.arquisoft.fichas.domain.observacionevaluacion.AgregacionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.ObservacionEvaluacionDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObservacionEvaluacionDuplicadaFinderImplTest {

    @Mock
    private ObservacionEvaluacionOutputPort observacionEvaluacionOutputPort;

    @InjectMocks
    private ObservacionEvaluacionDuplicadaFinderImpl finder;

    @Test
    void debeConsultarConElTextoRecortado_yRetornarLaExistenciaDelPuerto() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        var agregacion = AgregacionObservacionEvaluacionDomain.crear(
                ObservacionEvaluacionDomain.crear(evaluacionFichaPerfil, "  Observación válida  "),
                UtilUUID.generarNuevoUUID());
        when(observacionEvaluacionOutputPort.existePorEvaluacionYObservacion(evaluacionFichaPerfil, "Observación válida"))
                .thenReturn(true);

        // Act
        var yaExiste = finder.obtener(agregacion);

        // Assert
        assertThat(yaExiste).isTrue();
    }
}
