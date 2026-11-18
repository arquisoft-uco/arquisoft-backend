package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.AportarPlanEstimadoCommand;
import com.arquisoft.mapas_ruta.application.planestimado.command.usecase.AportarPlanEstimadoUseCase;
import com.arquisoft.mapas_ruta.domain.planestimado.AportePlanEstimadoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AportarPlanEstimadoInteractorImplTest {

    @Mock
    private AportarPlanEstimadoUseCase useCase;

    @InjectMocks
    private AportarPlanEstimadoInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnUseCase_cuandoCommandValido() {
        // Arrange
        var command = AportarPlanEstimadoCommand.crear(UtilUUID.generarNuevoUUID(), "Revisar");
        var captor = ArgumentCaptor.forClass(AportePlanEstimadoDomain.class);

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getPlanEstimado()).isEqualTo(command.planEstimado());
        assertThat(captor.getValue().getDescripcionEstimado()).isEqualTo("Revisar");
    }
}
