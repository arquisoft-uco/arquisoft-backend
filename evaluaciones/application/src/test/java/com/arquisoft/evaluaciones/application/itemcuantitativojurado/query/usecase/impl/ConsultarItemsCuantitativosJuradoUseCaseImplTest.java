package com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.secondaryport.ItemCuantitativoJuradoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoJuradoKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarItemsCuantitativosJuradoUseCaseImplTest {

    @Mock
    private ItemCuantitativoJuradoQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarItemsCuantitativosJuradoUseCaseImpl useCase;

    @Test
    void debeRetornarTodosLosReadModel_cuandoElPuertoLosEntrega() {
        // Arrange
        List<ItemCuantitativoJuradoReadModel> esperados = List.of(
                new ItemCuantitativoJuradoReadModel(UUID.randomUUID(), "Puntualidad", "Evalúa la puntualidad",
                        UUID.randomUUID(), 50),
                new ItemCuantitativoJuradoReadModel(UUID.randomUUID(), "Presentación", "Evalúa la presentación",
                        UUID.randomUUID(), 30));
        when(queryOutputPort.consultarTodos()).thenReturn(esperados);

        // Act
        List<ItemCuantitativoJuradoReadModel> resultado = useCase.ejecutar();

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
        List<ItemCuantitativoJuradoReadModel> resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeLoguearElTotalDeResultados_cuandoSeEjecuta() {
        // Arrange
        List<ItemCuantitativoJuradoReadModel> esperados = List.of(
                new ItemCuantitativoJuradoReadModel(UUID.randomUUID(), "Puntualidad", "Evalúa la puntualidad",
                        UUID.randomUUID(), 50),
                new ItemCuantitativoJuradoReadModel(UUID.randomUUID(), "Presentación", "Evalúa la presentación",
                        UUID.randomUUID(), 30));
        when(queryOutputPort.consultarTodos()).thenReturn(esperados);

        // Act
        useCase.ejecutar();

        // Assert
        verify(logger).debug(any(ItemCuantitativoJuradoKey.class), eq(2));
    }
}
