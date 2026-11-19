package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.ReanudarProyectoGradoCommand;
import com.arquisoft.proyectos.application.proyectogrado.command.result.ReanudacionProyectoGradoResult;
import com.arquisoft.proyectos.application.proyectogrado.command.usecase.ReanudarProyectoGradoUseCase;
import com.arquisoft.proyectos.domain.proyectogrado.ReanudacionProyectoGradoDomain;
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
class ReanudarProyectoGradoInteractorImplTest {

    @Mock
    private ReanudarProyectoGradoUseCase useCase;

    @InjectMocks
    private ReanudarProyectoGradoInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var command = new ReanudarProyectoGradoCommand(
                UtilUUID.generarNuevoUUID(), LocalDate.of(2026, 12, 1), LocalDate.of(2026, 11, 19));
        var esperado = new ReanudacionProyectoGradoResult.Reanudado(command.proyectoGrado());
        when(useCase.ejecutar(argThat((ReanudacionProyectoGradoDomain reanudacion) ->
                reanudacion.getProyectoGrado().equals(command.proyectoGrado())))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(argThat((ReanudacionProyectoGradoDomain reanudacion) ->
                reanudacion.getFechaFin().equals(command.fechaFin()) && reanudacion.getHoy().equals(command.hoy())));
    }
}
