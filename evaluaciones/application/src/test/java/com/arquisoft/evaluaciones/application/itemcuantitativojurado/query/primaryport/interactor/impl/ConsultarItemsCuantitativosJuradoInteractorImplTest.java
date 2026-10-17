package com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.usecase.ConsultarItemsCuantitativosJuradoUseCase;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarItemsCuantitativosJuradoInteractorImplTest {

    @Mock
    private ConsultarItemsCuantitativosJuradoUseCase useCase;

    @InjectMocks
    private ConsultarItemsCuantitativosJuradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoEjecuta() {
        // Arrange
        List<ItemCuantitativoJuradoReadModel> esperados = List.of(
                new ItemCuantitativoJuradoReadModel(UUID.randomUUID(), "Puntualidad", "Evalúa la puntualidad",
                        UUID.randomUUID(), 50));
        when(useCase.ejecutar()).thenReturn(esperados);

        // Act
        List<ItemCuantitativoJuradoReadModel> resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(useCase, times(1)).ejecutar();
    }

    @Test
    void debePropagarListaVacia_cuandoElUseCaseNoEncuentraRegistros() {
        // Arrange
        when(useCase.ejecutar()).thenReturn(List.of());

        // Act
        List<ItemCuantitativoJuradoReadModel> resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
        verify(useCase, times(1)).ejecutar();
    }
}
