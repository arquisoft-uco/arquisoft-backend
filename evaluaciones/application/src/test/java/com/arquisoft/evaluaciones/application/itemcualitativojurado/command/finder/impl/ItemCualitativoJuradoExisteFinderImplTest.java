package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemCualitativoJuradoExisteFinderImplTest {

    @Mock
    private ItemCualitativoJuradoOutputPort outputPort;

    @InjectMocks
    private ItemCualitativoJuradoExisteFinderImpl finder;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void debeDelegarEnPuerto_cuandoConsultaExistencia(boolean existe) {
        // Arrange
        UUID itemCualitativoJurado = UUID.randomUUID();
        when(outputPort.existePorId(itemCualitativoJurado)).thenReturn(existe);

        // Act
        Boolean resultado = finder.obtener(itemCualitativoJurado);

        // Assert
        assertThat(resultado).isEqualTo(existe);
        verify(outputPort).existePorId(itemCualitativoJurado);
    }
}
