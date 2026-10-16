package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.EliminarRespuestaUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EliminarRespuestaAmpliacionPlazoInteractorImplTest {

    @Mock
    private EliminarRespuestaUseCase useCase;

    @InjectMocks
    private EliminarRespuestaAmpliacionPlazoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoEjecutaElComando() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var command = EliminarRespuestaAmpliacionPlazoCommand.crear(solicitud.toString(), coordinador);

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(any());
    }
}
