package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.RegistrarObservacionItemJuradoCommand;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.RegistrarObservacionItemJuradoUseCase;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarObservacionItemJuradoInteractorImplTest {

    @Mock
    private RegistrarObservacionItemJuradoUseCase useCase;

    @InjectMocks
    private RegistrarObservacionItemJuradoInteractorImpl interactor;

    @Test
    void debeMapearDelegarYRetornarId_cuandoEjecutaCommand() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var command = RegistrarObservacionItemJuradoCommand.crear(
                evaluacionCuantitativaJurado, "Sustenta el puntaje otorgado");
        var id = UUID.randomUUID();
        when(useCase.ejecutar(any(ObservacionItemJuradoDomain.class))).thenReturn(id);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        ArgumentCaptor<ObservacionItemJuradoDomain> captor = ArgumentCaptor.forClass(ObservacionItemJuradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(resultado).isEqualTo(id);
        assertThat(captor.getValue().getEvaluacionCuantitativaJurado()).isEqualTo(evaluacionCuantitativaJurado);
        assertThat(captor.getValue().getDescripcion()).isEqualTo("Sustenta el puntaje otorgado");
    }
}
