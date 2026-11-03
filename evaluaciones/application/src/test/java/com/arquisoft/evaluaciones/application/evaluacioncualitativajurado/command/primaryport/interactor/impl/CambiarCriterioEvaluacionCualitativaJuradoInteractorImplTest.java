package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.CambiarCriterioEvaluacionCualitativaJuradoCommand;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.usecase.CambiarCriterioEvaluacionCualitativaJuradoUseCase;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.CambioCriterioEvaluacionCualitativaJuradoDomain;
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
class CambiarCriterioEvaluacionCualitativaJuradoInteractorImplTest {

    @Mock
    private CambiarCriterioEvaluacionCualitativaJuradoUseCase useCase;

    @InjectMocks
    private CambiarCriterioEvaluacionCualitativaJuradoInteractorImpl interactor;

    @Test
    void debeMapearYDelegarAlUseCase_cuandoSeEjecuta() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var command = new CambiarCriterioEvaluacionCualitativaJuradoCommand(evaluacion, criterio, jurado);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(CambioCriterioEvaluacionCualitativaJuradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEvaluacionCualitativaJurado()).isEqualTo(evaluacion);
        assertThat(captor.getValue().getCriterio()).isEqualTo(criterio);
        assertThat(captor.getValue().getJurado()).isEqualTo(jurado);
    }
}
