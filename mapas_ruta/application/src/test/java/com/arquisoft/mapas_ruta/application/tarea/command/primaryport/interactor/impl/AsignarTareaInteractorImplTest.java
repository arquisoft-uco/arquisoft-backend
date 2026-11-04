package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.AsignarTareaCommand;
import com.arquisoft.mapas_ruta.application.tarea.command.usecase.AsignarTareaUseCase;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignarTareaInteractorImplTest {

    @Mock
    private AsignarTareaUseCase useCase;

    @InjectMocks
    private AsignarTareaInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseYDevolverElId() {
        // Arrange
        var command = new AsignarTareaCommand(
                UtilUUID.generarNuevoUUID(), "Redactar capitulo", UtilUUID.generarNuevoUUID());
        var id = UtilUUID.generarNuevoUUID();
        when(useCase.ejecutar(any(TareaDomain.class))).thenReturn(id);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(id);
        verify(useCase).ejecutar(argThat((TareaDomain tarea) ->
                tarea.getMapaRuta().equals(command.mapaRuta())
                        && tarea.getAsesor().equals(command.asesor())
                        && tarea.getDescripcion().equals(command.descripcion())));
    }
}
