package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.ModificarObservacionItemJuradoCommand;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.ModificarObservacionItemJuradoUseCase;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ModificacionObservacionItemJuradoDomain;
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
class ModificarObservacionItemJuradoInteractorImplTest {

    @Mock
    private ModificarObservacionItemJuradoUseCase useCase;

    @InjectMocks
    private ModificarObservacionItemJuradoInteractorImpl interactor;

    @Test
    void debeMapearYDelegar_cuandoEjecutaCommand() {
        // Arrange
        var observacionItemJurado = UUID.randomUUID();
        var command = ModificarObservacionItemJuradoCommand.crear(
                observacionItemJurado, "Sustenta el puntaje otorgado");

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(ModificacionObservacionItemJuradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getObservacionItemJurado()).isEqualTo(observacionItemJurado);
        assertThat(captor.getValue().getDescripcion()).isEqualTo("Sustenta el puntaje otorgado");
    }
}
