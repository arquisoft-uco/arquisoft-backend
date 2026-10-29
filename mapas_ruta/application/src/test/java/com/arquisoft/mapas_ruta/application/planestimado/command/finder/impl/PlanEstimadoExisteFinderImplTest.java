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
class PlanEstimadoExisteFinderImplTest {

    @Mock
    private PlanEstimadoOutputPort planEstimadoOutputPort;

    @InjectMocks
    private PlanEstimadoExisteFinderImpl finder;

    @Test
    void debeDevolverLoQueRespondeElPuerto_cuandoConsultaPorId() {
        // Arrange
        var existente = UtilUUID.generarNuevoUUID();
        var inexistente = UtilUUID.generarNuevoUUID();
        when(planEstimadoOutputPort.existePorId(existente)).thenReturn(true);
        when(planEstimadoOutputPort.existePorId(inexistente)).thenReturn(false);

        // Act
        var existe = finder.obtener(existente);
        var noExiste = finder.obtener(inexistente);

        // Assert
        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}
