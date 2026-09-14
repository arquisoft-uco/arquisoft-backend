package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.ItemCuantitativoJuradoOutputPort;
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
class ItemCuantitativoJuradoExisteFinderImplTest {

    @Mock
    private ItemCuantitativoJuradoOutputPort outputPort;

    @InjectMocks
    private ItemCuantitativoJuradoExisteFinderImpl finder;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void debeDelegarEnPuerto_cuandoConsultaExistencia(boolean existe) {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();
        when(outputPort.existePorId(itemCuantitativoJurado)).thenReturn(existe);

        // Act
        Boolean resultado = finder.obtener(itemCuantitativoJurado);

        // Assert
        assertThat(resultado).isEqualTo(existe);
        verify(outputPort).existePorId(itemCuantitativoJurado);
    }
}
