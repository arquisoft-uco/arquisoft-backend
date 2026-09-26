package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.ParEvaluacionCualitativaJuradoCommand;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.RegistrarEvaluacionesCualitativasJuradoCommand;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.usecase.RegistrarEvaluacionesCualitativasJuradoUseCase;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.RegistroEvaluacionesCualitativasJuradoDomain;
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
class RegistrarEvaluacionesCualitativasJuradoInteractorImplTest {

    @Mock
    private RegistrarEvaluacionesCualitativasJuradoUseCase useCase;

    @InjectMocks
    private RegistrarEvaluacionesCualitativasJuradoInteractorImpl interactor;

    @Test
    void debeMapearYDelegarAlUseCase_cuandoEjecutaCommand() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID item = UUID.randomUUID();
        UUID criterio = UUID.randomUUID();
        var command = new RegistrarEvaluacionesCualitativasJuradoCommand(
                evaluacionJurado, List.of(new ParEvaluacionCualitativaJuradoCommand(item, criterio)));

        // Act
        interactor.ejecutar(command);

        // Assert
        ArgumentCaptor<RegistroEvaluacionesCualitativasJuradoDomain> captor =
                ArgumentCaptor.forClass(RegistroEvaluacionesCualitativasJuradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        RegistroEvaluacionesCualitativasJuradoDomain registro = captor.getValue();
        assertThat(registro.getEvaluaciones()).hasSize(1);
        assertThat(registro.getEvaluaciones().get(0).getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(registro.getEvaluaciones().get(0).getItem()).isEqualTo(item);
        assertThat(registro.getEvaluaciones().get(0).getCriterio()).isEqualTo(criterio);
    }
}
