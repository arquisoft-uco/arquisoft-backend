package com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.model.AsignarJuradosEvaluacionCommand;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.AsignarJuradosEvaluacionUseCase;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.AsignacionJuradosEvaluacionDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignarJuradosEvaluacionInteractorImplTest {

    @Mock
    private AsignarJuradosEvaluacionUseCase useCase;

    @InjectMocks
    private AsignarJuradosEvaluacionInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseYDevolverSusIds_cuandoSeEjecuta() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var command = new AsignarJuradosEvaluacionCommand(evaluacion, List.of(jurado));
        var ids = List.of(UtilUUID.generarNuevoUUID());
        when(useCase.ejecutar(any(AsignacionJuradosEvaluacionDomain.class))).thenReturn(ids);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(AsignacionJuradosEvaluacionDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEvaluacion()).isEqualTo(evaluacion);
        assertThat(captor.getValue().getJurados()).containsExactly(jurado);
        assertThat(resultado).isSameAs(ids);
    }
}
