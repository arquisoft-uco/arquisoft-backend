package com.arquisoft.usuarios.application.usuario.command.usecase.impl;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;
import com.arquisoft.usuarios.application.usuario.command.finder.EmailUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.primaryport.model.CrearUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.validator.CrearUsuarioValidator;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @Mock
    private AppLogger logger;

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
        verify(usuarioOutputPort, times(1)).guardar(any(UsuarioEntity.class));
    }

    @Test
    void debePublicarEvento_cuandoEjecucionExitosa() {
        // Arrange
        CrearUsuarioCommand command = new CrearUsuarioCommand("test@example.com", UsuarioRole.ASESOR_FICHA);
        when(emailUsuarioExisteFinder.obtener(anyString())).thenReturn(false);

        // Act
        crearUsuarioUseCase.ejecutar(command);

        // Assert
        verify(eventPublisher, times(1)).publish(any(DomainEvent.class));
    }

    @Test
    void debeMapearAggregateAEntity_cuandoGuarda() {
        // Arrange
        CrearUsuarioCommand command = new CrearUsuarioCommand("admin@example.com", UsuarioRole.ADMINISTRADOR);
        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        when(emailUsuarioExisteFinder.obtener(anyString())).thenReturn(false);

        // Act
        crearUsuarioUseCase.ejecutar(command);

        // Assert
        verify(usuarioOutputPort).guardar(captor.capture());
        UsuarioEntity guardado = captor.getValue();
        assertThat(guardado.email()).isEqualTo("admin@example.com");
        assertThat(guardado.rol()).isEqualTo(UsuarioRole.ADMINISTRADOR.getCodigo());
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
