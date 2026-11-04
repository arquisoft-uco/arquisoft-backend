package com.arquisoft.evaluaciones.application.jurado.query.finder.impl;

import com.arquisoft.evaluaciones.application.jurado.query.secondaryport.JuradoAccesoQueryOutputPort;
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
class JuradoExisteQueryFinderImplTest {

    @Mock
    private JuradoAccesoQueryOutputPort outputPort;

    @InjectMocks
    private JuradoExisteQueryFinderImpl finder;

    @Test
    void debeDelegarEnElOutputPort_yRetornarLoQueEsteResponda() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        when(outputPort.existePorId(jurado)).thenReturn(true);

        // Act
        var resultado = finder.obtener(jurado);

        // Assert
        assertThat(resultado).isTrue();
        verify(outputPort).existePorId(jurado);
    }
}
