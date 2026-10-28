package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.CambiarEstadoItemTareaCommand;
import com.arquisoft.mapas_ruta.application.tarea.command.usecase.CambiarEstadoItemTareaUseCase;
import com.arquisoft.mapas_ruta.domain.tarea.CambioEstadoItemTareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoItemTareaInteractorImplTest {

    @Mock
    private CambiarEstadoItemTareaUseCase useCase;

    @InjectMocks
    private CambiarEstadoItemTareaInteractorImpl interactor;

    @Test
    void debeConvertirElCommandYDelegarEnElUseCase() {
        // Arrange
        var command = new CambiarEstadoItemTareaCommand(UtilUUID.generarNuevoUUID(), "EN_PROGRESO");

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(argThat((CambioEstadoItemTareaDomain cambio) ->
                cambio.getTarea().equals(command.tarea())
                        && cambio.getEstadoItem() == EstadoItem.EN_PROGRESO));
    }
}
