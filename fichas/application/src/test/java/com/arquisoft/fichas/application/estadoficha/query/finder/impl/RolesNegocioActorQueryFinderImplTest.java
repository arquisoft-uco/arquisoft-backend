package com.arquisoft.fichas.application.estadoficha.query.finder.impl;

import com.arquisoft.fichas.application.estadoficha.query.secondaryport.RolesNegocioActorQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolesNegocioActorQueryFinderImplTest {

    @Mock
    private RolesNegocioActorQueryOutputPort outputPort;

    @InjectMocks
    private RolesNegocioActorQueryFinderImpl finder;

    @Test
    void debeRetornarLosRolesDelPuerto_cuandoElActorTieneRoles() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        when(outputPort.consultarPorActor(actor)).thenReturn(List.of("ASESOR_FICHA"));

        // Act
        var roles = finder.obtener(actor);

        // Assert
        assertThat(roles).containsExactly("ASESOR_FICHA");
        verify(outputPort, times(1)).consultarPorActor(actor);
    }
}
