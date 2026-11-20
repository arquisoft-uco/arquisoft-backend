package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.RegistrarResultadoEvaluacionEntregableProyectoGradoCommand;
import com.arquisoft.entregables.application.entregableproyectogrado.command.result.RegistroResultadoEvaluacionEntregableProyectoGradoResult;
import com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.RegistrarResultadoEvaluacionEntregableProyectoGradoUseCase;
import com.arquisoft.entregables.domain.entregableproyectogrado.ResultadoEvaluacionEntregableProyectoGradoDomain;
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
class RegistrarResultadoEvaluacionEntregableProyectoGradoInteractorImplTest {

    @Mock
    private RegistrarResultadoEvaluacionEntregableProyectoGradoUseCase useCase;

    @InjectMocks
    private RegistrarResultadoEvaluacionEntregableProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDominioMapeadoYDevolverElResult_cuandoEjecuta() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var command = new RegistrarResultadoEvaluacionEntregableProyectoGradoCommand(entregable, "APROBADO");
        var esperado = new RegistroResultadoEvaluacionEntregableProyectoGradoResult.Registrado(entregable, "APROBADO");
        when(useCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(ResultadoEvaluacionEntregableProyectoGradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(resultado).isSameAs(esperado);
        assertThat(captor.getValue().getEntregableProyectoGrado()).isEqualTo(entregable);
    }
}
