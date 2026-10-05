package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.RegistrarProyectoGradoCommand;
import com.arquisoft.proyectos.application.proyectogrado.command.result.RegistroProyectoGradoResult;
import com.arquisoft.proyectos.application.proyectogrado.command.usecase.RegistrarProyectoGradoUseCase;
import com.arquisoft.proyectos.domain.proyectogrado.RegistroProyectoGradoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarProyectoGradoInteractorImplTest {

    @Mock
    private RegistrarProyectoGradoUseCase useCase;

    @InjectMocks
    private RegistrarProyectoGradoInteractorImpl interactor;

    @Test
    void debeMapearElCommandAlRegistroYDevolverElResultado_cuandoSeEjecuta() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var command = new RegistrarProyectoGradoCommand(fichaPerfil, "Titulo", UUID.randomUUID(),
                List.of(UUID.randomUUID()));
        var esperado = new RegistroProyectoGradoResult.Duplicado(fichaPerfil);
        when(useCase.ejecutar(any(RegistroProyectoGradoDomain.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(RegistroProyectoGradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyecto().getFichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(resultado).isSameAs(esperado);
    }
}
