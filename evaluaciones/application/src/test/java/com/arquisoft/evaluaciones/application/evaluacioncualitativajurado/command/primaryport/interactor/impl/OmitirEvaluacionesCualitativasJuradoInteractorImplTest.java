package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.OmitirEvaluacionesCualitativasJuradoCommand;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.usecase.OmitirEvaluacionesCualitativasJuradoUseCase;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.OmisionEvaluacionesCualitativasJuradoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OmitirEvaluacionesCualitativasJuradoInteractorImplTest {

    @Mock
    private OmitirEvaluacionesCualitativasJuradoUseCase useCase;

    @InjectMocks
    private OmitirEvaluacionesCualitativasJuradoInteractorImpl interactor;

    @Test
    void debeMapearYDelegarAlUseCase_cuandoEjecutaCommand() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var evaluacion = UUID.randomUUID();
        var command = new OmitirEvaluacionesCualitativasJuradoCommand(evaluacionJurado, List.of(evaluacion));

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(OmisionEvaluacionesCualitativasJuradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(captor.getValue().getEvaluaciones()).containsExactly(evaluacion);
    }
}
