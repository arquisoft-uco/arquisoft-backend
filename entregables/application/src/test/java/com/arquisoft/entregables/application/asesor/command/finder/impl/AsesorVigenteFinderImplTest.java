package com.arquisoft.entregables.application.asesor.command.finder.impl;

import com.arquisoft.entregables.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsesorVigenteFinderImplTest {

    @Mock
    private AsesorOutputPort asesorOutputPort;

    @InjectMocks
    private AsesorVigenteFinderImpl finder;

    @Test
    void debeRetornarLoQueDiceElPuerto_cuandoSeConsultaLaVigencia() {
        // Arrange
        var vigente = UtilUUID.generarNuevoUUID();
        var noVigente = UtilUUID.generarNuevoUUID();
        when(asesorOutputPort.existeVigentePorId(vigente)).thenReturn(true);
        when(asesorOutputPort.existeVigentePorId(noVigente)).thenReturn(false);

        // Act
        var resultadoVigente = finder.obtener(vigente);
        var resultadoNoVigente = finder.obtener(noVigente);

        // Assert
        assertThat(resultadoVigente).isTrue();
        assertThat(resultadoNoVigente).isFalse();
    }
}
