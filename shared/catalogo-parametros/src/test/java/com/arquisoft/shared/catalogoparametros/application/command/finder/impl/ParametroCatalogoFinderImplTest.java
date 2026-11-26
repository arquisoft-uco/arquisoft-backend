package com.arquisoft.shared.catalogoparametros.application.command.finder.impl;

import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.ParametrosCatalogoOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParametroCatalogoFinderImplTest {

    @Mock
    private ParametrosCatalogoOutputPort puerto;

    @InjectMocks
    private ParametroCatalogoFinderImpl finder;

    @Test
    void debeDevolverElValor_delPuerto() {
        // Arrange
        when(puerto.valor("PARAM_X")).thenReturn("valor");

        // Act
        var resultado = finder.obtener("PARAM_X");

        // Assert
        assertThat(resultado).isEqualTo("valor");
        verify(puerto, times(1)).valor("PARAM_X");
    }
}
