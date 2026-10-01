package com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.usecase.RegistrarUsuarioUseCase;
import com.arquisoft.usuarios.domain.usuario.RegistroUsuarioDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioInteractorImplTest {

    @Mock
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    private RegistrarUsuarioInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElRegistroMapeadoDelCommand() {
        // Arrange
        interactor = new RegistrarUsuarioInteractorImpl(registrarUsuarioUseCase);
        var command = RegistrarUsuarioCommand.crear(
                "usr001", "Ana", "Pérez", "ana@uco.edu.co", "573001112233", List.of("estudiante"));
        var idEsperado = UUID.randomUUID();
        when(registrarUsuarioUseCase.ejecutar(org.mockito.ArgumentMatchers.any())).thenReturn(idEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(idEsperado);
        var captor = ArgumentCaptor.forClass(RegistroUsuarioDomain.class);
        verify(registrarUsuarioUseCase, times(1)).ejecutar(captor.capture());
        assertThat(captor.getValue().getIdentificador()).isEqualTo("usr001");
        assertThat(captor.getValue().getNombre()).isEqualTo("Ana Pérez");
        assertThat(captor.getValue().getRoles()).containsExactly("estudiante");
    }
}
