package com.arquisoft.mapas_ruta.application.planestimado.command.finder.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.PlanEstimadoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiasEstimadosAcumuladosDeMapaRutaFinderImplTest {

    @Mock
    private PlanEstimadoOutputPort planEstimadoOutputPort;

    @InjectMocks
    private DiasEstimadosAcumuladosDeMapaRutaFinderImpl finder;

    @Test
    void debeDevolverLaSumaDelPuerto_cuandoConsultaPorTarea() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        when(planEstimadoOutputPort.sumarDiasEstimadosPorMapaRutaDeTarea(tarea)).thenReturn(12L);

        // Act
        var dias = finder.obtener(tarea);

        // Assert
        assertThat(dias).isEqualTo(12L);
        verify(planEstimadoOutputPort).sumarDiasEstimadosPorMapaRutaDeTarea(tarea);
    }
}
