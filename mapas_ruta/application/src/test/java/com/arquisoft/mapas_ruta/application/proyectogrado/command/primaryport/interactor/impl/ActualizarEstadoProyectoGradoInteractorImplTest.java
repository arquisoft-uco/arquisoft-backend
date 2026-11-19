package com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.model.ActualizarEstadoProyectoGradoCommand;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.result.ActualizacionEstadoProyectoGradoResult;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.usecase.ActualizarEstadoProyectoGradoUseCase;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ActualizacionEstadoProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarEstadoProyectoGradoInteractorImplTest {

    @Mock
    private ActualizarEstadoProyectoGradoUseCase useCase;

    @InjectMocks
    private ActualizarEstadoProyectoGradoInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var command = new ActualizarEstadoProyectoGradoCommand(
                UtilUUID.generarNuevoUUID(), "ATRASADO", Instant.parse("2026-11-01T10:00:00Z"));
        var esperado = new ActualizacionEstadoProyectoGradoResult.Actualizado(command.proyectoGrado(), "ATRASADO");
        when(useCase.ejecutar(argThat((ActualizacionEstadoProyectoGradoDomain actualizacion) ->
                actualizacion.getProyectoGrado().equals(command.proyectoGrado())))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(argThat((ActualizacionEstadoProyectoGradoDomain actualizacion) ->
                actualizacion.getEstadoProyecto().getId().equals("ATRASADO")));
    }
}
