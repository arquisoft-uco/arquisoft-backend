package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.ModificarPlanEstimadoCommand;
import com.arquisoft.mapas_ruta.application.planestimado.command.usecase.ModificarPlanEstimadoUseCase;
import com.arquisoft.mapas_ruta.domain.planestimado.ModificacionPlanEstimadoDomain;
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
class ModificarPlanEstimadoInteractorImplTest {

    @Mock
    private ModificarPlanEstimadoUseCase useCase;

    @InjectMocks
    private ModificarPlanEstimadoInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnUseCase_cuandoCommandValido() {
        // Arrange
        var command = new ModificarPlanEstimadoCommand(UtilUUID.generarNuevoUUID(), "Revisar");
        var captor = ArgumentCaptor.forClass(ModificacionPlanEstimadoDomain.class);

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getPlanEstimado()).isEqualTo(command.planEstimado());
        assertThat(captor.getValue().getDescripcionEstimado()).isEqualTo("Revisar");
    }
}
