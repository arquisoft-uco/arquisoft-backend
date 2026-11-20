package com.arquisoft.biblioteca.application.entregableproyectogrado.command.finder.impl;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntregableProyectoGradoExisteFinderImplTest {

    @Mock
    private EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @InjectMocks
    private EntregableProyectoGradoExisteFinderImpl finder;

    @Test
    void debeResponderLoQueDiceElPuerto_cuandoConsultaPorId() {
        // Arrange
        var existente = UtilUUID.generarNuevoUUID();
        var ausente = UtilUUID.generarNuevoUUID();
        when(entregableProyectoGradoOutputPort.existePorId(existente)).thenReturn(true);
        when(entregableProyectoGradoOutputPort.existePorId(ausente)).thenReturn(false);

        // Act & Assert
        assertThat(finder.obtener(existente)).isTrue();
        assertThat(finder.obtener(ausente)).isFalse();
    }
}
