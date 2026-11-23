package com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObservacionesEvaluacionCountFinderImplTest {

    @Mock
    private ObservacionEvaluacionOutputPort observacionEvaluacionOutputPort;

    @InjectMocks
    private ObservacionesEvaluacionCountFinderImpl finder;

    @Test
    void debeRetornarElConteoDelPuerto_cuandoConsultaPorEvaluacion() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(observacionEvaluacionOutputPort.contarPorEvaluacion(evaluacion)).thenReturn(3L);

        // Act
        var observaciones = finder.obtener(evaluacion);

        // Assert
        assertThat(observaciones).isEqualTo(3L);
    }
}
