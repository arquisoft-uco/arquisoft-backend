package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.AgregarPlanEstimadoCommand;
import com.arquisoft.mapas_ruta.application.planestimado.command.usecase.AgregarPlanEstimadoUseCase;
import com.arquisoft.mapas_ruta.domain.planestimado.PlanEstimadoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarPlanEstimadoInteractorImplTest {

    @Mock
    private AgregarPlanEstimadoUseCase useCase;

    @InjectMocks
    private AgregarPlanEstimadoInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnUseCase_cuandoCommandValido() {
        // Arrange
        var command = new AgregarPlanEstimadoCommand(UtilUUID.generarNuevoUUID(), 3, "Revisar");
        var id = UtilUUID.generarNuevoUUID();
        when(useCase.ejecutar(any(PlanEstimadoDomain.class))).thenReturn(id);
        var captor = ArgumentCaptor.forClass(PlanEstimadoDomain.class);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(captor.capture());
        assertThat(resultado).isEqualTo(id);
        assertThat(captor.getValue().getTarea()).isEqualTo(command.tarea());
        assertThat(captor.getValue().getTiempoEstimadoDias()).isEqualTo(3);
        assertThat(captor.getValue().getDescripcionEstimado()).isEqualTo("Revisar");
    }
}
