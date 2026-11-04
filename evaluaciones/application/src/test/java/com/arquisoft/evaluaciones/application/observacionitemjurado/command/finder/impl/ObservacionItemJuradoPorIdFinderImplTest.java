package com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.ObservacionItemJuradoOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.entity.ObservacionItemJuradoEntity;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObservacionItemJuradoPorIdFinderImplTest {

    @Mock
    private ObservacionItemJuradoOutputPort outputPort;

    @InjectMocks
    private ObservacionItemJuradoPorIdFinderImpl finder;

    @Test
    void debeRetornarDomainMapeado_cuandoLaObservacionExiste() {
        // Arrange
        var id = UUID.randomUUID();
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var entity = new ObservacionItemJuradoEntity(id, evaluacionCuantitativaJurado, "Sustenta el puntaje otorgado");
        when(outputPort.obtenerPorId(id)).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getEvaluacionCuantitativaJurado()).isEqualTo(evaluacionCuantitativaJurado);
        assertThat(resultado.getDescripcion()).isEqualTo("Sustenta el puntaje otorgado");
        verify(outputPort, times(1)).obtenerPorId(id);
    }

    @Test
    void debeRetornarVacio_cuandoLaObservacionNoExiste() {
        // Arrange
        var id = UUID.randomUUID();
        when(outputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isEqualTo(ObservacionItemJuradoDomain.VACIO);
        assertThat(resultado.esVacio()).isTrue();
    }
}
