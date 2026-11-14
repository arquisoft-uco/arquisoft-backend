package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.RemoverObservacionItemJuradoCommand;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.RemoverObservacionItemJuradoUseCase;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.RemocionObservacionItemJuradoDomain;
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
class RemoverObservacionItemJuradoInteractorImplTest {

    @Mock
    private RemoverObservacionItemJuradoUseCase useCase;

    @InjectMocks
    private RemoverObservacionItemJuradoInteractorImpl interactor;

    @Test
    void debeMapearYDelegar_cuandoEjecutaCommand() {
        // Arrange
        var observacionItemJurado = UtilUUID.generarNuevoUUID();
        var command = RemoverObservacionItemJuradoCommand.crear(observacionItemJurado);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(RemocionObservacionItemJuradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getObservacionItemJurado()).isEqualTo(observacionItemJurado);
    }
}
