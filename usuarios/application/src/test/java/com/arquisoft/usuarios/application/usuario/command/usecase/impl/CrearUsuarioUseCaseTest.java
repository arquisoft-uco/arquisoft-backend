package com.arquisoft.usuarios.application.usuario.command.usecase.impl;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.events.EventPublisher;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;
import com.arquisoft.usuarios.application.usuario.command.finder.EmailUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.primaryport.model.CrearUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.validator.CrearUsuarioValidator;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearUsuarioUseCaseTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;

    @Mock
    private EmailUsuarioExisteFinder emailUsuarioExisteFinder;

    @Mock
    private CrearUsuarioValidator crearUsuarioValidator;

    @Mock
    private EventPublisher eventPublisher;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

@InjectMocks
    private CrearUsuarioUseCaseImpl crearUsuarioUseCase;

    @Test
    void debeCrearUsuario_cuandoDatosValidos() {
        // Arrange
        CrearUsuarioCommand command = new CrearUsuarioCommand("test@example.com", UsuarioRole.ESTUDIANTE);
        when(emailUsuarioExisteFinder.obtener(anyString())).thenReturn(false);

        // Act
        UUID resultado = crearUsuarioUseCase.ejecutar(command);

        // Assert
        assertThat(resultado).isNotNull();
        verify(usuarioOutputPort, times(1)).save(any(UsuarioDomain.class));
    }

    @Test
    void debePublicarEventoDrenado_cuandoEjecucionExitosa() {
        // Arrange
        CrearUsuarioCommand command = new CrearUsuarioCommand("test@example.com", UsuarioRole.ASESOR_FICHA);
        when(emailUsuarioExisteFinder.obtener(anyString())).thenReturn(false);

        // Act
        crearUsuarioUseCase.ejecutar(command);

        // Assert
        verify(eventPublisher, times(1)).publish(any(DomainEvent.class));
    }

    @Test
    void debeGuardarAggregate_cuandoEjecutar() {
        // Arrange
        CrearUsuarioCommand command = new CrearUsuarioCommand("admin@example.com", UsuarioRole.ADMINISTRADOR);
        ArgumentCaptor<UsuarioDomain> captor = ArgumentCaptor.forClass(UsuarioDomain.class);
        when(emailUsuarioExisteFinder.obtener(anyString())).thenReturn(false);

        // Act
        crearUsuarioUseCase.ejecutar(command);

        // Assert
        verify(usuarioOutputPort).save(captor.capture());
        UsuarioDomain saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("admin@example.com");
        assertThat(saved.getRol()).isEqualTo(UsuarioRole.ADMINISTRADOR);
    }

    @Test
    void debeLanzarExcepcion_cuandoEmailEsNulo() {
        // Arrange
        CrearUsuarioCommand command = new CrearUsuarioCommand(null, UsuarioRole.COORDINADOR);

        // Act / Assert
        assertThatThrownBy(() -> crearUsuarioUseCase.ejecutar(command))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("email");
    }

    @Test
    void debeLanzarExcepcion_cuandoRolEsNulo() {
        // Arrange
        CrearUsuarioCommand command = new CrearUsuarioCommand("test@example.com", null);

        // Act / Assert
        assertThatThrownBy(() -> crearUsuarioUseCase.ejecutar(command))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("rol");
    }
}
