package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.AtrasarProyectoGradoCommand;
import com.arquisoft.proyectos.application.proyectogrado.command.result.AtrasoProyectoGradoResult;
import com.arquisoft.proyectos.application.proyectogrado.command.usecase.AtrasarProyectoGradoUseCase;
import com.arquisoft.proyectos.domain.proyectogrado.AtrasoProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtrasarProyectoGradoInteractorImplTest {

    @Mock
    private AtrasarProyectoGradoUseCase useCase;

    @InjectMocks
    private AtrasarProyectoGradoInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var command = new AtrasarProyectoGradoCommand(UtilUUID.generarNuevoUUID());
        var esperado = new AtrasoProyectoGradoResult.Atrasado(command.proyectoGrado());
        when(useCase.ejecutar(argThat((AtrasoProyectoGradoDomain atraso) ->
                atraso.getProyectoGrado().equals(command.proyectoGrado())))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(argThat((AtrasoProyectoGradoDomain atraso) ->
                atraso.getProyectoGrado().equals(command.proyectoGrado())));
    }
}
