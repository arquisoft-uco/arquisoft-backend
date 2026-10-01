package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NombreItemCualitativoJuradoExisteFinderImplTest {

    @Mock
    private ItemCualitativoJuradoOutputPort outputPort;

    @InjectMocks
    private NombreItemCualitativoJuradoExisteFinderImpl finder;

    @Test
    void debeRetornarResultadoDelPuerto_cuandoConsultaNombre() {
        // Arrange
        String nombre = "Claridad";
        when(outputPort.existePorNombreIgnorandoMayusculas(nombre)).thenReturn(false);

        // Act
        Boolean resultado = finder.obtener(nombre);

        // Assert
        assertThat(resultado).isFalse();
        verify(outputPort).existePorNombreIgnorandoMayusculas(nombre);
    }
}
