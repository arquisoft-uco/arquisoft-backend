package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.OmitirEvaluacionesCuantitativasJuradoCommand;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.usecase.OmitirEvaluacionesCuantitativasJuradoUseCase;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.OmisionEvaluacionesCuantitativasJuradoDomain;
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
class OmitirEvaluacionesCuantitativasJuradoInteractorImplTest {

    @Mock
    private OmitirEvaluacionesCuantitativasJuradoUseCase useCase;

    @InjectMocks
    private OmitirEvaluacionesCuantitativasJuradoInteractorImpl interactor;

    @Test
    void debeMapearYDelegarAlUseCase_cuandoEjecutaCommand() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var evaluacion = UUID.randomUUID();
        var command = new OmitirEvaluacionesCuantitativasJuradoCommand(evaluacionJurado, List.of(evaluacion));

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(OmisionEvaluacionesCuantitativasJuradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(captor.getValue().getEvaluaciones()).containsExactly(evaluacion);
    }
}
