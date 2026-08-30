package com.arquisoft.evaluaciones.application.itemcualitativojurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.secondaryport.ItemCualitativoJuradoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarItemsCualitativosJuradoUseCaseImplTest {

    @Mock
    private ItemCualitativoJuradoQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarItemsCualitativosJuradoUseCaseImpl useCase;

    @Test
    void debeRetornarTodosLosReadModel_cuandoElPuertoLosEntrega() {
        // Arrange
        List<ItemCualitativoJuradoReadModel> esperados = List.of(
                new ItemCualitativoJuradoReadModel(UUID.randomUUID(), "Claridad", "Evalúa la claridad conceptual"),
                new ItemCualitativoJuradoReadModel(UUID.randomUUID(), "Rigor", "Evalúa el rigor metodológico")
        );
        when(queryOutputPort.consultarTodos()).thenReturn(esperados);

        // Act
        List<ItemCualitativoJuradoReadModel> resultado = useCase.ejecutar(null);

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(queryOutputPort, times(1)).consultarTodos();
        verifyNoMoreInteractions(queryOutputPort);
    }

    @Test
    void debeRetornarListaVacia_cuandoElPuertoNoEncuentraRegistros() {
        // Arrange
        when(queryOutputPort.consultarTodos()).thenReturn(List.of());

        // Act
        List<ItemCualitativoJuradoReadModel> resultado = useCase.ejecutar(null);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
