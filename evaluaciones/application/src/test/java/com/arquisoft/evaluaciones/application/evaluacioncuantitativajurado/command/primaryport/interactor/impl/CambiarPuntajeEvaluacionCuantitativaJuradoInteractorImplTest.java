package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.CambiarPuntajeEvaluacionCuantitativaJuradoCommand;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.usecase.CambiarPuntajeEvaluacionCuantitativaJuradoUseCase;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.CambioPuntajeEvaluacionCuantitativaJuradoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CambiarPuntajeEvaluacionCuantitativaJuradoInteractorImplTest {

    @Mock
    private CambiarPuntajeEvaluacionCuantitativaJuradoUseCase useCase;

    @InjectMocks
    private CambiarPuntajeEvaluacionCuantitativaJuradoInteractorImpl interactor;

    @Test
    void debeMapearYDelegar_cuandoEjecutaCommand() {
        // Arrange
        UUID evaluacionCuantitativaJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        var command = CambiarPuntajeEvaluacionCuantitativaJuradoCommand.crear(
                evaluacionCuantitativaJurado, 400, jurado.toString());

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(CambioPuntajeEvaluacionCuantitativaJuradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEvaluacionCuantitativaJurado()).isEqualTo(evaluacionCuantitativaJurado);
        assertThat(captor.getValue().getJurado()).isEqualTo(jurado);
        assertThat(captor.getValue().getNuevoPuntaje()).isEqualTo(400);
    }
}
