package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.RemoverPlanRealCommand;
import com.arquisoft.mapas_ruta.application.planreal.command.usecase.RemoverPlanRealUseCase;
import com.arquisoft.mapas_ruta.domain.planreal.RemocionPlanRealDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoverPlanRealInteractorImplTest {

    @Mock
    private RemoverPlanRealUseCase useCase;

    @InjectMocks
    private RemoverPlanRealInteractorImpl interactor;

    @Test
    void debeConvertirYDelegarAlUseCase_cuandoCommandValido() {
        // Arrange
        var command = new RemoverPlanRealCommand(UtilUUID.generarNuevoUUID());

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(argThat((RemocionPlanRealDomain remocion) ->
                remocion.getPlanReal().equals(command.planReal())));
    }
}
