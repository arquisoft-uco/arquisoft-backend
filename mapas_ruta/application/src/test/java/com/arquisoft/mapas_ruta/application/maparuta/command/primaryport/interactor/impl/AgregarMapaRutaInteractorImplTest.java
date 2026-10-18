package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.AgregarMapaRutaCommand;
import com.arquisoft.mapas_ruta.application.maparuta.command.usecase.AgregarMapaRutaUseCase;
import com.arquisoft.mapas_ruta.domain.maparuta.AgregacionMapaRutaDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarMapaRutaInteractorImplTest {

    @Mock
    private AgregarMapaRutaUseCase useCase;

    @InjectMocks
    private AgregarMapaRutaInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var command = new AgregarMapaRutaCommand(
                proyectoGrado, coordinador, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));
        var id = UtilUUID.generarNuevoUUID();
        when(useCase.ejecutar(argThat((AgregacionMapaRutaDomain agregacion) ->
                agregacion.getCoordinador().equals(coordinador)
                        && agregacion.getMapaRuta().getProyectoGrado().equals(proyectoGrado)))).thenReturn(id);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(id);
        verify(useCase).ejecutar(argThat((AgregacionMapaRutaDomain agregacion) ->
                agregacion.getMapaRuta().getFechaFin().equals(command.fechaFin())));
    }
}
