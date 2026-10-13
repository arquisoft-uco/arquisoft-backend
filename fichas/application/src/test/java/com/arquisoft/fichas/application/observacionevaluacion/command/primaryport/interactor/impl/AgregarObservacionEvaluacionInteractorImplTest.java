package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.AgregarObservacionEvaluacionCommand;
import com.arquisoft.fichas.application.observacionevaluacion.command.usecase.AgregarObservacionEvaluacionUseCase;
import com.arquisoft.fichas.domain.observacionevaluacion.AgregacionObservacionEvaluacionDomain;
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
class AgregarObservacionEvaluacionInteractorImplTest {

    @Mock
    private AgregarObservacionEvaluacionUseCase agregarObservacionEvaluacionUseCase;

    @InjectMocks
    private AgregarObservacionEvaluacionInteractorImpl agregarObservacionEvaluacionInteractor;

    @Test
    void debeDelegarEnUseCase_conDominioMapeado() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();
        var command = AgregarObservacionEvaluacionCommand.crear(
                evaluacionFichaPerfil, "Observación válida", representanteComite);
        var observacionEvaluacionId = UtilUUID.generarNuevoUUID();
        when(agregarObservacionEvaluacionUseCase.ejecutar(any(AgregacionObservacionEvaluacionDomain.class)))
                .thenReturn(observacionEvaluacionId);

        // Act
        var resultado = agregarObservacionEvaluacionInteractor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(observacionEvaluacionId);
        var captor = ArgumentCaptor.forClass(AgregacionObservacionEvaluacionDomain.class);
        verify(agregarObservacionEvaluacionUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEvaluacionFichaPerfil()).isEqualTo(evaluacionFichaPerfil);
        assertThat(captor.getValue().getRepresentanteComite()).isEqualTo(representanteComite);
    }
}
