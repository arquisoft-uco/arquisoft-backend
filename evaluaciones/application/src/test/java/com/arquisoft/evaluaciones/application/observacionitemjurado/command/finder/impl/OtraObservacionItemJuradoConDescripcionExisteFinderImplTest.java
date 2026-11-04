package com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.ObservacionItemJuradoOutputPort;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ModificacionObservacionItemJuradoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OtraObservacionItemJuradoConDescripcionExisteFinderImplTest {

    @Mock
    private ObservacionItemJuradoOutputPort outputPort;

    @InjectMocks
    private OtraObservacionItemJuradoConDescripcionExisteFinderImpl finder;

    @Test
    void debeDelegarConIdYDescripcionDeLaModificacion_cuandoConsultaSiExisteOtra() {
        // Arrange
        var observacionItemJurado = UUID.randomUUID();
        var modificacion = ModificacionObservacionItemJuradoDomain.crear(
                observacionItemJurado, "  Descripción repetida  ");
        when(outputPort.existeOtraConDescripcion(observacionItemJurado, "Descripción repetida")).thenReturn(true);

        // Act
        var resultado = finder.obtener(modificacion);

        // Assert
        assertThat(resultado).isTrue();
        verify(outputPort, times(1)).existeOtraConDescripcion(observacionItemJurado, "Descripción repetida");
    }
}
