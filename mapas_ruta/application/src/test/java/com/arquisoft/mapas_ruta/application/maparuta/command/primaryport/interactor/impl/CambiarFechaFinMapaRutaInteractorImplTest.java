package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.CambiarFechaFinMapaRutaCommand;
import com.arquisoft.mapas_ruta.application.maparuta.command.usecase.CambiarFechaFinMapaRutaUseCase;
import com.arquisoft.mapas_ruta.domain.maparuta.CambioFechaFinMapaRutaDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CambiarFechaFinMapaRutaInteractorImplTest {

    @Mock
    private CambiarFechaFinMapaRutaUseCase useCase;

    @InjectMocks
    private CambiarFechaFinMapaRutaInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase() {
        // Arrange
        var command = new CambiarFechaFinMapaRutaCommand(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), LocalDate.of(2026, 12, 15));

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(argThat((CambioFechaFinMapaRutaDomain cambio) ->
                cambio.getMapaRuta().equals(command.mapaRuta())
                        && cambio.getCoordinador().equals(command.coordinador())
                        && cambio.getFechaFin().equals(command.fechaFin())));
    }
}
