package com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.model.AgregarProyectoGradoCommand;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.result.AgregacionProyectoGradoResult;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.usecase.AgregarProyectoGradoUseCase;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
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
class AgregarProyectoGradoInteractorImplTest {

    @Mock
    private AgregarProyectoGradoUseCase useCase;

    @InjectMocks
    private AgregarProyectoGradoInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var command = new AgregarProyectoGradoCommand(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Titulo", UtilUUID.generarNuevoUUID(), Instant.parse("2026-11-01T10:00:00Z"));
        var esperado = new AgregacionProyectoGradoResult.Agregado(command.proyectoGrado());
        when(useCase.ejecutar(argThat((ProyectoGradoDomain proyecto) ->
                proyecto.getId().equals(command.proyectoGrado())))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(argThat((ProyectoGradoDomain proyecto) ->
                proyecto.getCoordinador().equals(command.coordinador())));
    }
}
