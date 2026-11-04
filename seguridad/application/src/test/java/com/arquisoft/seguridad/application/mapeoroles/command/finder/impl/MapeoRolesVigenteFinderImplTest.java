package com.arquisoft.seguridad.application.mapeoroles.command.finder.impl;

import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.MapeoRolesOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapeoRolesVigenteFinderImplTest {

    @Mock
    private MapeoRolesOutputPort mapeoRolesOutputPort;

    @Test
    void debeDelegarEnElPuerto() {
        // Arrange
        when(mapeoRolesOutputPort.estaVigente()).thenReturn(true);

        // Act
        var vigente = new MapeoRolesVigenteFinderImpl(mapeoRolesOutputPort).obtener();

        // Assert
        assertThat(vigente).isTrue();
        verify(mapeoRolesOutputPort).estaVigente();
    }
}
