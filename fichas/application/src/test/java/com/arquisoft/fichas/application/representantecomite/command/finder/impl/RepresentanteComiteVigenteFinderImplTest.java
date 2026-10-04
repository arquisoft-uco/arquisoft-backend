package com.arquisoft.fichas.application.representantecomite.command.finder.impl;

import com.arquisoft.fichas.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
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
class RepresentanteComiteVigenteFinderImplTest {

    @Mock
    private RepresentanteComiteOutputPort representanteComiteOutputPort;

    @InjectMocks
    private RepresentanteComiteVigenteFinderImpl finder;

    @Test
    void debeRetornarTrue_cuandoElRepresentanteEstaVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(representanteComiteOutputPort.existeVigentePorId(id)).thenReturn(true);

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isTrue();
        verify(representanteComiteOutputPort).existeVigentePorId(id);
    }

    @Test
    void debeRetornarFalse_cuandoElRepresentanteNoEstaVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(representanteComiteOutputPort.existeVigentePorId(id)).thenReturn(false);

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isFalse();
    }
}
