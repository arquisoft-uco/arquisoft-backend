package com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.readmodel.CriterioItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.usecase.ConsultarCriteriosItemCualitativoAsesorUseCase;
import com.arquisoft.shared.util.UtilUUID;
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
class ConsultarCriteriosItemCualitativoAsesorInteractorImplTest {

    @Mock
    private ConsultarCriteriosItemCualitativoAsesorUseCase useCase;

    @InjectMocks
    private ConsultarCriteriosItemCualitativoAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var esperados = List.of(
                new CriterioItemCualitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Actitud", "Evalúa la actitud"));
        when(useCase.ejecutar()).thenReturn(esperados);

        // Act
        var resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(esperados);
        verify(useCase, times(1)).ejecutar();
    }
}
