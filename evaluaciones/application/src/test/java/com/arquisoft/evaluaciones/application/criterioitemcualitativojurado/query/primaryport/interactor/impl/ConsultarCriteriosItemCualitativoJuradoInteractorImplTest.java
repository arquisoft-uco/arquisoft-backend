package com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.usecase.ConsultarCriteriosItemCualitativoJuradoUseCase;
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
class ConsultarCriteriosItemCualitativoJuradoInteractorImplTest {

    @Mock
    private ConsultarCriteriosItemCualitativoJuradoUseCase useCase;

    @InjectMocks
    private ConsultarCriteriosItemCualitativoJuradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoEjecuta() {
        // Arrange
        List<CriterioItemCualitativoJuradoReadModel> esperados = List.of(
                new CriterioItemCualitativoJuradoReadModel(UUID.randomUUID(), "Claridad", "Evalúa la claridad conceptual")
        );
        when(useCase.ejecutar(null)).thenReturn(esperados);

        // Act
        List<CriterioItemCualitativoJuradoReadModel> resultado = interactor.ejecutar(null);

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(useCase).ejecutar(null);
    }

    @Test
    void debePropagarListaVacia_cuandoElUseCaseNoEncuentraRegistros() {
        // Arrange
        when(useCase.ejecutar(null)).thenReturn(List.of());

        // Act
        List<CriterioItemCualitativoJuradoReadModel> resultado = interactor.ejecutar(null);

        // Assert
        assertThat(resultado).isEmpty();
        verify(useCase).ejecutar(null);
    }
}
