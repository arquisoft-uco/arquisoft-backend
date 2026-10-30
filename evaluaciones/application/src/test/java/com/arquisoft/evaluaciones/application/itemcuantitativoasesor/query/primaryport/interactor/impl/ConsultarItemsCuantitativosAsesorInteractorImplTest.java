package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.readmodel.ItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.usecase.ConsultarItemsCuantitativosAsesorUseCase;
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
class ConsultarItemsCuantitativosAsesorInteractorImplTest {

    @Mock
    private ConsultarItemsCuantitativosAsesorUseCase useCase;

    @InjectMocks
    private ConsultarItemsCuantitativosAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCase_cuandoSeEjecuta() {
        // Arrange
        var esperados = List.of(
                new ItemCuantitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Puntualidad",
                        "Evalúa la puntualidad", UtilUUID.generarNuevoUUID(), 50));
        when(useCase.ejecutar()).thenReturn(esperados);

        // Act
        var resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(esperados);
        verify(useCase, times(1)).ejecutar();
    }
}
