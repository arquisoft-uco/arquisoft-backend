package com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.usuario.command.primaryport.model.CrearUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.usecase.CrearUsuarioUseCase;
import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearUsuarioInteractorImplTest {

    @Mock
    private CrearUsuarioUseCase crearUsuarioUseCase;

    @InjectMocks
    private CrearUsuarioInteractorImpl crearUsuarioInteractor;

    @Test
    void debeDelegarEnElUseCase_cuandoEjecutar() {
        // Arrange
        CrearUsuarioCommand command = new CrearUsuarioCommand("test@example.com", UsuarioRole.ESTUDIANTE);
        UUID idEsperado = UUID.randomUUID();
        when(crearUsuarioUseCase.ejecutar(command)).thenReturn(idEsperado);

        // Act
        UUID resultado = crearUsuarioInteractor.ejecutar(command);

        // Assert
        assertThat(resultado).isEqualTo(idEsperado);
        verify(crearUsuarioUseCase).ejecutar(command);
    }
}
