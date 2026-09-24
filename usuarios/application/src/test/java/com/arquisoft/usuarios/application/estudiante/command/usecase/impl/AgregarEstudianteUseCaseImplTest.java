package com.arquisoft.usuarios.application.estudiante.command.usecase.impl;

import com.arquisoft.usuarios.application.estudiante.command.finder.EstudiantePorUsuarioFinder;
import com.arquisoft.usuarios.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.usuarios.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.usuarios.application.estudiante.command.validator.AgregarEstudianteValidator;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.estudiante.event.EstudianteAgregadoEvent;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.estudiante.exception.EstudianteUsuarioDuplicadoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarEstudianteUseCaseImplTest {

    @Mock
    private EstudianteOutputPort estudianteOutputPort;
    @Mock
    private EstudiantePorUsuarioFinder estudiantePorUsuarioFinder;
    @Mock
    private AgregarEstudianteValidator agregarEstudianteValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private AgregarEstudianteUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarEstudianteUseCaseImpl(
                estudianteOutputPort, estudiantePorUsuarioFinder,
                agregarEstudianteValidator,
                eventPublisher, logger);
    }

    private UsuarioDomain usuario() {
        return UsuarioDomain.reconstruir(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO);
    }

    @Test
    void debeGuardarYPublicar_cuandoUsuarioNoEsEstudiante() {
        // Arrange
        var usuario = usuario();
        when(estudiantePorUsuarioFinder.obtener(usuario.getId())).thenReturn(EstudianteDomain.VACIO);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        var captorEntity = ArgumentCaptor.forClass(EstudianteEntity.class);
        verify(estudianteOutputPort, times(1)).guardar(captorEntity.capture());
        assertThat(captorEntity.getValue().usuario()).isEqualTo(usuario.getId());

        var captorEvento = ArgumentCaptor.forClass(EstudianteAgregadoEvent.class);
        verify(eventPublisher, times(1)).publish(captorEvento.capture());
        assertThat(captorEvento.getValue().getUsuario()).isEqualTo(usuario.getId());
        assertThat(captorEvento.getValue().getIdentificador()).isEqualTo(usuario.getIdentificador());
        assertThat(captorEvento.getValue().getNombre()).isEqualTo(usuario.getNombre());
        assertThat(captorEvento.getValue().getEmail()).isEqualTo(usuario.getEmail());

        verify(estudiantePorUsuarioFinder, times(1)).obtener(usuario.getId());
    }

    @Test
    void debeInvocarFinderAntesDelValidator_cuandoAgrega() {
        // Arrange
        var usuario = usuario();
        when(estudiantePorUsuarioFinder.obtener(any())).thenReturn(EstudianteDomain.VACIO);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        InOrder orden = inOrder(estudiantePorUsuarioFinder, agregarEstudianteValidator,
                estudianteOutputPort, eventPublisher);
        orden.verify(estudiantePorUsuarioFinder).obtener(usuario.getId());
        orden.verify(agregarEstudianteValidator).validar(usuario.getId(), EstudianteDomain.VACIO);
        orden.verify(estudianteOutputPort).guardar(any());
        orden.verify(eventPublisher).publish(any());
    }

    @Test
    void debePropagarExcepcion_cuandoValidatorLanza() {
        // Arrange
        var usuario = usuario();
        when(estudiantePorUsuarioFinder.obtener(any())).thenReturn(EstudianteDomain.VACIO);
        doThrow(new EstudianteUsuarioDuplicadoException(usuario.getId()))
                .when(agregarEstudianteValidator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(usuario))
                .isInstanceOf(EstudianteUsuarioDuplicadoException.class);
        verify(estudianteOutputPort, never()).guardar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeReactivarYPublicarSinGuardar_cuandoEstudianteEstaRemovido() {
        // Arrange
        var usuario = usuario();
        var removido = EstudianteDomain.reconstruir(usuario.getId(), Instant.parse("2026-09-01T10:00:00Z"));
        when(estudiantePorUsuarioFinder.obtener(usuario.getId())).thenReturn(removido);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        verify(estudiantePorUsuarioFinder, times(1)).obtener(usuario.getId());
        verify(agregarEstudianteValidator).validar(usuario.getId(), removido);
        verify(estudianteOutputPort, times(1)).reactivar(usuario.getId());
        verify(estudianteOutputPort, never()).guardar(any());
        assertThat(removido.estaEliminado()).isFalse();
        var captorEvento = ArgumentCaptor.forClass(EstudianteAgregadoEvent.class);
        verify(eventPublisher, times(1)).publish(captorEvento.capture());
        assertThat(captorEvento.getValue().getUsuario()).isEqualTo(usuario.getId());
    }
}
