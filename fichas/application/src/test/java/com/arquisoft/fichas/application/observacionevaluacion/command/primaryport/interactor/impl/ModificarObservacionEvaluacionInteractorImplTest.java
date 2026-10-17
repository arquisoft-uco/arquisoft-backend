package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.ModificarObservacionEvaluacionCommand;
import com.arquisoft.fichas.application.observacionevaluacion.command.usecase.ModificarObservacionEvaluacionUseCase;
import com.arquisoft.fichas.domain.observacionevaluacion.ModificacionObservacionEvaluacionDomain;
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
class ModificarObservacionEvaluacionInteractorImplTest {

    @Mock
    private ModificarObservacionEvaluacionUseCase modificarObservacionEvaluacionUseCase;

    @InjectMocks
    private ModificarObservacionEvaluacionInteractorImpl modificarObservacionEvaluacionInteractor;

    @Test
    void debeDelegarEnUseCase_conDominioMapeado() {
        // Arrange
        var observacionEvaluacion = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();
        var command = ModificarObservacionEvaluacionCommand.crear(
                observacionEvaluacion, "Nuevo texto de la observación", representanteComite);

        // Act
        modificarObservacionEvaluacionInteractor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(ModificacionObservacionEvaluacionDomain.class);
        verify(modificarObservacionEvaluacionUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getObservacionEvaluacion()).isEqualTo(observacionEvaluacion);
        assertThat(captor.getValue().getObservacion()).isEqualTo("Nuevo texto de la observación");
        assertThat(captor.getValue().getRepresentanteComite()).isEqualTo(representanteComite);
    }
}
