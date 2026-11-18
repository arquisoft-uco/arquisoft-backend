package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.ItemCualitativoAsesorOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NombreItemCualitativoAsesorExisteFinderImplTest {

    @Mock
    private ItemCualitativoAsesorOutputPort outputPort;

    @InjectMocks
    private NombreItemCualitativoAsesorExisteFinderImpl finder;

    @Test
    void debeDelegarEnElPuerto_cuandoConsultaNombre() {
        // Arrange
        var nombre = "Claridad";
        when(outputPort.existePorNombreIgnorandoMayusculas(nombre)).thenReturn(true);

        // Act
        var resultado = finder.obtener(nombre);

        // Assert
        assertThat(resultado).isTrue();
        verify(outputPort).existePorNombreIgnorandoMayusculas(nombre);
    }
}
