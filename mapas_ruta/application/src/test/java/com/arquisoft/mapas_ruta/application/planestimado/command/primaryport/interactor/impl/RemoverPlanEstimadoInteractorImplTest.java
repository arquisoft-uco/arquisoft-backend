package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.RemoverPlanEstimadoCommand;
import com.arquisoft.mapas_ruta.application.planestimado.command.usecase.RemoverPlanEstimadoUseCase;
import com.arquisoft.mapas_ruta.domain.planestimado.RemocionPlanEstimadoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoverPlanEstimadoInteractorImplTest {

    @Mock
    private RemoverPlanEstimadoUseCase useCase;

    @InjectMocks
    private RemoverPlanEstimadoInteractorImpl interactor;

    @Test
    void debeConvertirYDelegarAlUseCase_cuandoCommandValido() {
        // Arrange
        var command = new RemoverPlanEstimadoCommand(UtilUUID.generarNuevoUUID());

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(argThat((RemocionPlanEstimadoDomain remocion) ->
                remocion.getPlanEstimado().equals(command.planEstimado())));
    }
}
