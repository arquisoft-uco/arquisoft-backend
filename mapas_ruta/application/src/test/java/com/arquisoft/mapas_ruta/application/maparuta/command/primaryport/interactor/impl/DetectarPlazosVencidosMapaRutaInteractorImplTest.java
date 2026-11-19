package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.DetectarPlazosVencidosMapaRutaCommand;
import com.arquisoft.mapas_ruta.application.maparuta.command.usecase.DetectarPlazosVencidosMapaRutaUseCase;
import com.arquisoft.mapas_ruta.domain.maparuta.DeteccionPlazosVencidosMapaRutaDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DetectarPlazosVencidosMapaRutaInteractorImplTest {

    @Mock
    private DetectarPlazosVencidosMapaRutaUseCase useCase;

    @InjectMocks
    private DetectarPlazosVencidosMapaRutaInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var hoy = LocalDate.of(2026, 11, 19);

        // Act
        interactor.ejecutar(new DetectarPlazosVencidosMapaRutaCommand(hoy));

        // Assert
        verify(useCase).ejecutar(argThat((DeteccionPlazosVencidosMapaRutaDomain deteccion) ->
                deteccion.getHoy().equals(hoy)));
    }
}
