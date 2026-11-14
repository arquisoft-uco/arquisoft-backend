package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.CambiarCriterioEvaluacionCualitativaAsesorCommand;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.usecase.CambiarCriterioEvaluacionCualitativaAsesorUseCase;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.CambioCriterioEvaluacionCualitativaAsesorDomain;
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
class CambiarCriterioEvaluacionCualitativaAsesorInteractorImplTest {

    @Mock
    private CambiarCriterioEvaluacionCualitativaAsesorUseCase useCase;

    @InjectMocks
    private CambiarCriterioEvaluacionCualitativaAsesorInteractorImpl interactor;

    @Test
    void debeDelegarAlUseCaseConElDomain() {
        // Arrange
        var evaluacionCualitativa = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        var command = CambiarCriterioEvaluacionCualitativaAsesorCommand.crear(
                evaluacionCualitativa, criterio.toString(), asesor);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(CambioCriterioEvaluacionCualitativaAsesorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEvaluacionCualitativaAsesor()).isEqualTo(evaluacionCualitativa);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(captor.getValue().getCriterio()).isEqualTo(criterio);
    }
}
