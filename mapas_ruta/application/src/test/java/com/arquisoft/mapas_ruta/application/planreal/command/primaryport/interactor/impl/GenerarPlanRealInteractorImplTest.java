package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.GenerarPlanRealCommand;
import com.arquisoft.mapas_ruta.application.planreal.command.result.GeneracionPlanRealResult;
import com.arquisoft.mapas_ruta.application.planreal.command.usecase.GenerarPlanRealUseCase;
import com.arquisoft.mapas_ruta.domain.planreal.GeneracionPlanRealDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerarPlanRealInteractorImplTest {

    @Mock
    private GenerarPlanRealUseCase useCase;

    @InjectMocks
    private GenerarPlanRealInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnUseCase_cuandoCommandValido() {
        // Arrange
        var command = new GenerarPlanRealCommand(UtilUUID.generarNuevoUUID(), Instant.parse("2026-11-05T10:00:00Z"));
        var esperado = new GeneracionPlanRealResult.Descartada(command.tarea());
        when(useCase.ejecutar(any(GeneracionPlanRealDomain.class))).thenReturn(esperado);
        var captor = ArgumentCaptor.forClass(GeneracionPlanRealDomain.class);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(captor.capture());
        assertThat(resultado).isSameAs(esperado);
        assertThat(captor.getValue().getTarea()).isEqualTo(command.tarea());
        assertThat(captor.getValue().getFinalizadaEn()).isEqualTo(command.finalizadaEn());
    }
}
