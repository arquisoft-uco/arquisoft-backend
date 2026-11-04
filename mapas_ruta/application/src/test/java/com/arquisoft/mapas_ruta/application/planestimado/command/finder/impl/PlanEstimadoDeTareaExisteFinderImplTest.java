package com.arquisoft.mapas_ruta.application.planestimado.command.finder.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.PlanEstimadoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanEstimadoDeTareaExisteFinderImplTest {

    @Mock
    private PlanEstimadoOutputPort planEstimadoOutputPort;

    @InjectMocks
    private PlanEstimadoDeTareaExisteFinderImpl finder;

    @Test
    void debeDevolverLoQueRespondeElPuerto_cuandoConsultaPorTarea() {
        // Arrange
        var conPlan = UtilUUID.generarNuevoUUID();
        var sinPlan = UtilUUID.generarNuevoUUID();
        when(planEstimadoOutputPort.existePorTarea(conPlan)).thenReturn(true);
        when(planEstimadoOutputPort.existePorTarea(sinPlan)).thenReturn(false);

        // Act
        var existe = finder.obtener(conPlan);
        var noExiste = finder.obtener(sinPlan);

        // Assert
        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}
