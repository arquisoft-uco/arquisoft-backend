package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.ModificarInformacionTareaCommand;
import com.arquisoft.mapas_ruta.application.tarea.command.usecase.ModificarInformacionTareaUseCase;
import com.arquisoft.mapas_ruta.domain.tarea.ModificacionTareaDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModificarInformacionTareaInteractorImplTest {

    @Mock
    private ModificarInformacionTareaUseCase useCase;

    @InjectMocks
    private ModificarInformacionTareaInteractorImpl interactor;

    @Test
    void debeConvertirYDelegarAlUseCase_cuandoCommandValido() {
        // Arrange
        var command = new ModificarInformacionTareaCommand(
                UtilUUID.generarNuevoUUID(), "Redactar capitulo", UtilUUID.generarNuevoUUID());

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(argThat((ModificacionTareaDomain modificacion) ->
                modificacion.getTarea().equals(command.tarea())
                        && modificacion.getDescripcion().equals(command.descripcion())
                        && modificacion.getAsesor().equals(command.asesor())));
    }
}
