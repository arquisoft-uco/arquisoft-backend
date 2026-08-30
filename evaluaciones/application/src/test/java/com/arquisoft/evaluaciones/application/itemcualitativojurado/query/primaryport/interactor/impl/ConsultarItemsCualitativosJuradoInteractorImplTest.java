package com.arquisoft.evaluaciones.application.itemcualitativojurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.usecase.ConsultarItemsCualitativosJuradoUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarItemsCualitativosJuradoInteractorImplTest {

    @Mock
    private ConsultarItemsCualitativosJuradoUseCase useCase;

    @InjectMocks
    private ConsultarItemsCualitativosJuradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoEjecuta() {
        // Arrange
        List<ItemCualitativoJuradoReadModel> esperados = List.of(
                new ItemCualitativoJuradoReadModel(UUID.randomUUID(), "Claridad", "Evalúa la claridad conceptual")
        );
        when(useCase.ejecutar(null)).thenReturn(esperados);

        // Act
        List<ItemCualitativoJuradoReadModel> resultado = interactor.ejecutar(null);

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(useCase).ejecutar(null);
    }

    @Test
    void debePropagarListaVacia_cuandoElUseCaseNoEncuentraRegistros() {
        // Arrange
        when(useCase.ejecutar(null)).thenReturn(List.of());

        // Act
        List<ItemCualitativoJuradoReadModel> resultado = interactor.ejecutar(null);

        // Assert
        assertThat(resultado).isEmpty();
        verify(useCase).ejecutar(null);
    }
}
