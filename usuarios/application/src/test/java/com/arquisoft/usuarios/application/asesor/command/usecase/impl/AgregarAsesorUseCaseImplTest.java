package com.arquisoft.usuarios.application.asesor.command.usecase.impl;

import com.arquisoft.usuarios.application.asesor.command.finder.AsesorPorUsuarioFinder;
import com.arquisoft.usuarios.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.usuarios.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.usuarios.application.asesor.command.validator.AgregarAsesorValidator;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesor.event.AsesorAgregadoEvent;
import com.arquisoft.usuarios.domain.asesor.exception.AsesorUsuarioDuplicadoException;
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
class AgregarAsesorUseCaseImplTest {

    @Mock
    private AsesorOutputPort asesorOutputPort;
    @Mock
    private AsesorPorUsuarioFinder asesorPorUsuarioFinder;
    @Mock
    private AgregarAsesorValidator agregarAsesorValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private AgregarAsesorUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarAsesorUseCaseImpl(
                asesorOutputPort, asesorPorUsuarioFinder, agregarAsesorValidator, eventPublisher, logger);
    }

    private UsuarioDomain usuario() {
        return UsuarioDomain.reconstruir(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO);
    }

    @Test
    void debeGuardarYPublicar_cuandoUsuarioNoEsAsesor() {
        // Arrange
        var usuario = usuario();
        when(asesorPorUsuarioFinder.obtener(usuario.getId())).thenReturn(AsesorDomain.VACIO);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        var captorEntity = ArgumentCaptor.forClass(AsesorEntity.class);
        verify(asesorOutputPort, times(1)).guardar(captorEntity.capture());
        assertThat(captorEntity.getValue().usuario()).isEqualTo(usuario.getId());

        var captorEvento = ArgumentCaptor.forClass(AsesorAgregadoEvent.class);
        verify(eventPublisher, times(1)).publish(captorEvento.capture());
        assertThat(captorEvento.getValue().getUsuario()).isEqualTo(usuario.getId());
        assertThat(captorEvento.getValue().getIdentificador()).isEqualTo(usuario.getIdentificador());
        assertThat(captorEvento.getValue().getNombre()).isEqualTo(usuario.getNombre());
        assertThat(captorEvento.getValue().getEmail()).isEqualTo(usuario.getEmail());

        verify(asesorPorUsuarioFinder, times(1)).obtener(usuario.getId());
    }

    @Test
    void debeInvocarFinderAntesDelValidator_cuandoAgrega() {
        // Arrange
        var usuario = usuario();
        when(asesorPorUsuarioFinder.obtener(any())).thenReturn(AsesorDomain.VACIO);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        InOrder orden = inOrder(asesorPorUsuarioFinder, agregarAsesorValidator,
                asesorOutputPort, eventPublisher);
        orden.verify(asesorPorUsuarioFinder).obtener(usuario.getId());
        orden.verify(agregarAsesorValidator).validar(usuario.getId(), AsesorDomain.VACIO);
        orden.verify(asesorOutputPort).guardar(any());
        orden.verify(eventPublisher).publish(any());
    }

    @Test
    void debePropagarExcepcion_cuandoValidatorLanza() {
        // Arrange
        var usuario = usuario();
        when(asesorPorUsuarioFinder.obtener(any())).thenReturn(AsesorDomain.crear(usuario.getId()));
        doThrow(new AsesorUsuarioDuplicadoException(usuario.getId()))
                .when(agregarAsesorValidator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(usuario))
                .isInstanceOf(AsesorUsuarioDuplicadoException.class);
        verify(asesorOutputPort, never()).guardar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeReactivarYPublicarSinGuardar_cuandoAsesorEstaRemovido() {
        // Arrange
        var usuario = usuario();
        var removido = AsesorDomain.reconstruir(usuario.getId(), Instant.parse("2026-09-01T10:00:00Z"));
        when(asesorPorUsuarioFinder.obtener(usuario.getId())).thenReturn(removido);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        verify(asesorPorUsuarioFinder, times(1)).obtener(usuario.getId());
        verify(agregarAsesorValidator).validar(usuario.getId(), removido);
        verify(asesorOutputPort, times(1)).reactivar(usuario.getId());
        verify(asesorOutputPort, never()).guardar(any());
        assertThat(removido.estaEliminado()).isFalse();
        var captorEvento = ArgumentCaptor.forClass(AsesorAgregadoEvent.class);
        verify(eventPublisher, times(1)).publish(captorEvento.capture());
        assertThat(captorEvento.getValue().getUsuario()).isEqualTo(usuario.getId());
    }
}
