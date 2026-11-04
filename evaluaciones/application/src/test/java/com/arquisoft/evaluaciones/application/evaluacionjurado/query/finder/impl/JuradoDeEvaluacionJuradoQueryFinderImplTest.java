package com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionJuradoAccesoQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JuradoDeEvaluacionJuradoQueryFinderImplTest {

    @Mock
    private EvaluacionJuradoAccesoQueryOutputPort outputPort;

    @InjectMocks
    private JuradoDeEvaluacionJuradoQueryFinderImpl finder;

    @Test
    void debeRetornarJurado_cuandoExiste() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerJuradoPorId(evaluacionJurado)).thenReturn(Optional.of(jurado));

        // Act
        var resultado = finder.obtener(evaluacionJurado);

        // Assert
        assertThat(resultado).isEqualTo(jurado);
    }

    @Test
    void debeRetornarUuidPorDefecto_cuandoNoExiste() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerJuradoPorId(evaluacionJurado)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(evaluacionJurado);

        // Assert
        assertThat(UtilUUID.esPorDefecto(resultado)).isTrue();
    }
}
