package com.arquisoft.fichas.application.estadoficha.query.usecase.impl;

import com.arquisoft.fichas.application.estadoficha.query.secondaryport.EstadoFichaQueryOutputPort;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosFichaUseCaseTest {

    @Mock
    private EstadoFichaQueryOutputPort queryOutputPort;

    @InjectMocks
    private ConsultarEstadosFichaUseCaseImpl useCase;

    @Test
    void debeRetornarListaCompleta_cuandoExistenEstados() {
        // Arrange
        List<EstadoFichaReadModel> estadosEsperados = List.of(
                new EstadoFichaReadModel("EN_CONSTRUCCION", "En Construccion", "Ficha en desarrollo"),
                new EstadoFichaReadModel("APROBADA", "Aprobada", "Ficha aprobada por el comite"),
                new EstadoFichaReadModel("NO_APROBADA", "No Aprobada", "Ficha rechazada")
        );
        when(queryOutputPort.findAll()).thenReturn(estadosEsperados);

        // Act
        List<EstadoFichaReadModel> resultado = useCase.ejecutar(null);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(3);
        assertThat(resultado).containsExactlyElementsOf(estadosEsperados);
        verify(queryOutputPort, times(1)).findAll();
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayEstados() {
        // Arrange
        when(queryOutputPort.findAll()).thenReturn(List.of());

        // Act
        List<EstadoFichaReadModel> resultado = useCase.ejecutar(null);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
        verify(queryOutputPort, times(1)).findAll();
    }
}
