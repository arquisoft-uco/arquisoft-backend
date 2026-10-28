package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.RemoverInformacionTareaCommand;
import com.arquisoft.mapas_ruta.application.tarea.command.usecase.RemoverInformacionTareaUseCase;
import com.arquisoft.mapas_ruta.domain.tarea.RemocionTareaDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoverInformacionTareaInteractorImplTest {

    @Mock
    private RemoverInformacionTareaUseCase useCase;

    @InjectMocks
    private RemoverInformacionTareaInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCase_conElObjetoDeAccion() {
        // Arrange
        var command = new RemoverInformacionTareaCommand(UtilUUID.generarNuevoUUID());

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(argThat((RemocionTareaDomain remocion) ->
                remocion.getTarea().equals(command.tarea())));
    }
}
