package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.ModificarPlanRealCommand;
import com.arquisoft.mapas_ruta.application.planreal.command.usecase.ModificarPlanRealUseCase;
import com.arquisoft.mapas_ruta.domain.planreal.ModificacionPlanRealDomain;
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
class ModificarPlanRealInteractorImplTest {

    @Mock
    private ModificarPlanRealUseCase useCase;

    @InjectMocks
    private ModificarPlanRealInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnUseCase_cuandoCommandValido() {
        // Arrange
        var command = new ModificarPlanRealCommand(UtilUUID.generarNuevoUUID(), 4, "Revisar");
        var captor = ArgumentCaptor.forClass(ModificacionPlanRealDomain.class);

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getPlanReal()).isEqualTo(command.planReal());
        assertThat(captor.getValue().getTiempoRealDias()).isEqualTo(4);
        assertThat(captor.getValue().getDescripcionReal()).isEqualTo("Revisar");
    }
}
