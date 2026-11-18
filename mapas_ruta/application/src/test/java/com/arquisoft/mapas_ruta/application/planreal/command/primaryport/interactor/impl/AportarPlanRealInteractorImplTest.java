package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.AportarPlanRealCommand;
import com.arquisoft.mapas_ruta.application.planreal.command.usecase.AportarPlanRealUseCase;
import com.arquisoft.mapas_ruta.domain.planreal.AportePlanRealDomain;
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
class AportarPlanRealInteractorImplTest {

    @Mock
    private AportarPlanRealUseCase useCase;

    @InjectMocks
    private AportarPlanRealInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnUseCase_cuandoCommandValido() {
        // Arrange
        var command = AportarPlanRealCommand.crear(UtilUUID.generarNuevoUUID(), "Revisar");
        var captor = ArgumentCaptor.forClass(AportePlanRealDomain.class);

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getPlanReal()).isEqualTo(command.planReal());
        assertThat(captor.getValue().getDescripcionReal()).isEqualTo("Revisar");
    }
}
