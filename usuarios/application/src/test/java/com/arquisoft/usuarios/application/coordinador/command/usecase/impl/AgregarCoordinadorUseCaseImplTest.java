package com.arquisoft.usuarios.application.coordinador.command.usecase.impl;

import com.arquisoft.usuarios.application.coordinador.command.finder.CoordinadorPorUsuarioFinder;
import com.arquisoft.usuarios.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.usuarios.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.usuarios.application.coordinador.command.validator.AgregarCoordinadorValidator;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.coordinador.event.CoordinadorAgregadoEvent;
import com.arquisoft.usuarios.domain.coordinador.exception.CoordinadorUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
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
class AgregarCoordinadorUseCaseImplTest {

    @Mock
    private CoordinadorOutputPort coordinadorOutputPort;
    @Mock
    private CoordinadorPorUsuarioFinder coordinadorPorUsuarioFinder;
    @Mock
    private AgregarCoordinadorValidator agregarCoordinadorValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private AgregarCoordinadorUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarCoordinadorUseCaseImpl(
                coordinadorOutputPort, coordinadorPorUsuarioFinder, agregarCoordinadorValidator,
                eventPublisher, logger);
    }

    private UsuarioDomain usuario() {
        return UsuarioDomain.reconstruir(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO);
    }

    @Test
    void debeGuardarYPublicar_cuandoUsuarioNoEsCoordinador() {
        // Arrange
        var usuario = usuario();
        when(coordinadorPorUsuarioFinder.obtener(usuario.getId())).thenReturn(CoordinadorDomain.VACIO);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        var captorEntity = ArgumentCaptor.forClass(CoordinadorEntity.class);
        verify(coordinadorOutputPort, times(1)).guardar(captorEntity.capture());
        assertThat(captorEntity.getValue().usuario()).isEqualTo(usuario.getId());

        var captorEvento = ArgumentCaptor.forClass(CoordinadorAgregadoEvent.class);
        verify(eventPublisher, times(1)).publish(captorEvento.capture());
        assertThat(captorEvento.getValue().getUsuario()).isEqualTo(usuario.getId());
        assertThat(captorEvento.getValue().getIdentificador()).isEqualTo(usuario.getIdentificador());
        assertThat(captorEvento.getValue().getNombre()).isEqualTo(usuario.getNombre());
        assertThat(captorEvento.getValue().getEmail()).isEqualTo(usuario.getEmail());

        verify(coordinadorPorUsuarioFinder, times(1)).obtener(usuario.getId());
    }

    @Test
    void debeInvocarFinderAntesDelValidator_cuandoAgrega() {
        // Arrange
        var usuario = usuario();
        when(coordinadorPorUsuarioFinder.obtener(any())).thenReturn(CoordinadorDomain.VACIO);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        InOrder orden = inOrder(coordinadorPorUsuarioFinder, agregarCoordinadorValidator,
                coordinadorOutputPort, eventPublisher);
        orden.verify(coordinadorPorUsuarioFinder).obtener(usuario.getId());
        orden.verify(agregarCoordinadorValidator).validar(usuario.getId(), CoordinadorDomain.VACIO);
        orden.verify(coordinadorOutputPort).guardar(any());
        orden.verify(eventPublisher).publish(any());
    }

    @Test
    void debePropagarExcepcion_cuandoValidatorLanza() {
        // Arrange
        var usuario = usuario();
        when(coordinadorPorUsuarioFinder.obtener(any())).thenReturn(CoordinadorDomain.crear(usuario.getId()));
        doThrow(new CoordinadorUsuarioDuplicadoException(usuario.getId()))
                .when(agregarCoordinadorValidator).validar(any(), any(CoordinadorDomain.class));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(usuario))
                .isInstanceOf(CoordinadorUsuarioDuplicadoException.class);
        verify(coordinadorOutputPort, never()).guardar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeReactivarYPublicarSinGuardar_cuandoCoordinadorEstaRemovido() {
        // Arrange
        var usuario = usuario();
        var removido = CoordinadorDomain.reconstruir(usuario.getId(), Instant.parse("2026-09-01T10:00:00Z"));
        when(coordinadorPorUsuarioFinder.obtener(usuario.getId())).thenReturn(removido);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        verify(coordinadorPorUsuarioFinder, times(1)).obtener(usuario.getId());
        verify(agregarCoordinadorValidator).validar(usuario.getId(), removido);
        verify(coordinadorOutputPort, times(1)).reactivar(usuario.getId());
        verify(coordinadorOutputPort, never()).guardar(any());
        assertThat(removido.estaEliminado()).isFalse();
        var captorEvento = ArgumentCaptor.forClass(CoordinadorAgregadoEvent.class);
        verify(eventPublisher, times(1)).publish(captorEvento.capture());
        assertThat(captorEvento.getValue().getUsuario()).isEqualTo(usuario.getId());
    }
}
