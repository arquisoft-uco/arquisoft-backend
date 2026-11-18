package com.arquisoft.mapas_ruta.application.planreal.command.finder.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.PlanRealOutputPort;
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
class PlanRealExisteFinderImplTest {

    @Mock
    private PlanRealOutputPort planRealOutputPort;

    @InjectMocks
    private PlanRealExisteFinderImpl finder;

    @Test
    void debeDelegarEnPuerto_cuandoConsultaPorId() {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();
        when(planRealOutputPort.existePorId(plan)).thenReturn(true);

        // Act
        var existe = finder.obtener(plan);

        // Assert
        assertThat(existe).isTrue();
        verify(planRealOutputPort).existePorId(plan);
    }
}
