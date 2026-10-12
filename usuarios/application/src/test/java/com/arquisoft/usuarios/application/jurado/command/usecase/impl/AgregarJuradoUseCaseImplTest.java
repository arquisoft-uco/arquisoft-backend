package com.arquisoft.usuarios.application.jurado.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.jurado.command.finder.JuradoPorUsuarioFinder;
import com.arquisoft.usuarios.application.jurado.command.secondaryport.JuradoOutputPort;
import com.arquisoft.usuarios.application.jurado.command.secondaryport.entity.JuradoEntity;
import com.arquisoft.usuarios.application.jurado.command.validator.AgregarJuradoValidator;
import com.arquisoft.usuarios.domain.jurado.JuradoDomain;
import com.arquisoft.usuarios.domain.jurado.event.JuradoAgregadoEvent;
import com.arquisoft.usuarios.domain.jurado.exception.JuradoUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

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
class AgregarJuradoUseCaseImplTest {

    @Mock
    private JuradoOutputPort juradoOutputPort;
    @Mock
    private JuradoPorUsuarioFinder juradoPorUsuarioFinder;
    @Mock
    private AgregarJuradoValidator agregarJuradoValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private AgregarJuradoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarJuradoUseCaseImpl(
                juradoOutputPort, juradoPorUsuarioFinder,
                agregarJuradoValidator, eventPublisher, logger);
    }

    private UsuarioDomain usuario() {
        return UsuarioDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void debeGuardarYPublicarEnOrden_cuandoUsuarioNoEsJurado() {
        // Arrange
        var usuario = usuario();
        when(juradoPorUsuarioFinder.obtener(usuario.getId())).thenReturn(JuradoDomain.VACIO);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        var orden = inOrder(juradoPorUsuarioFinder, agregarJuradoValidator,
                juradoOutputPort, eventPublisher);
        orden.verify(juradoPorUsuarioFinder).obtener(usuario.getId());
        orden.verify(agregarJuradoValidator).validar(usuario.getId(), JuradoDomain.VACIO);
        orden.verify(juradoOutputPort).guardar(any());
        orden.verify(eventPublisher).publish(any());

        var captorEntity = ArgumentCaptor.forClass(JuradoEntity.class);
        verify(juradoOutputPort, times(1)).guardar(captorEntity.capture());
        assertThat(captorEntity.getValue().usuario()).isEqualTo(usuario.getId());
        assertThat(captorEntity.getValue().eliminadoEn()).isEqualTo(UtilFecha.VACIO);

        var captorEvento = ArgumentCaptor.forClass(JuradoAgregadoEvent.class);
        verify(eventPublisher, times(1)).publish(captorEvento.capture());
        assertThat(captorEvento.getValue().getUsuario()).isEqualTo(usuario.getId());
        assertThat(captorEvento.getValue().getIdentificador()).isEqualTo(usuario.getIdentificador());
        assertThat(captorEvento.getValue().getNombre()).isEqualTo(usuario.getNombre());
        assertThat(captorEvento.getValue().getEmail()).isEqualTo(usuario.getEmail());

        verify(juradoPorUsuarioFinder, times(1)).obtener(usuario.getId());
        verify(juradoOutputPort, never()).reactivar(any());
    }

    @Test
    void debeReactivarYPublicarSinGuardar_cuandoElJuradoEstaEliminado() {
        // Arrange
        var usuario = usuario();
        var eliminado = JuradoDomain.reconstruir(usuario.getId(), Instant.parse("2026-09-24T10:00:00Z"));
        when(juradoPorUsuarioFinder.obtener(usuario.getId())).thenReturn(eliminado);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        var orden = inOrder(agregarJuradoValidator, juradoOutputPort, eventPublisher);
        orden.verify(agregarJuradoValidator).validar(usuario.getId(), eliminado);
        orden.verify(juradoOutputPort).reactivar(usuario.getId());
        orden.verify(eventPublisher).publish(any(JuradoAgregadoEvent.class));
        verify(juradoOutputPort, never()).guardar(any());
        verify(juradoPorUsuarioFinder, times(1)).obtener(usuario.getId());
        assertThat(eliminado.estaEliminado()).isFalse();
    }

    @Test
    void debePropagarYNoEscribirNiPublicar_cuandoElUsuarioYaEsJuradoVigente() {
        // Arrange
        var usuario = usuario();
        var vigente = JuradoDomain.crear(usuario.getId());
        when(juradoPorUsuarioFinder.obtener(usuario.getId())).thenReturn(vigente);
        doThrow(new JuradoUsuarioDuplicadoException(usuario.getId()))
                .when(agregarJuradoValidator).validar(usuario.getId(), vigente);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(usuario))
                .isInstanceOf(JuradoUsuarioDuplicadoException.class);
        verify(juradoOutputPort, never()).guardar(any());
        verify(juradoOutputPort, never()).reactivar(any());
        verify(eventPublisher, never()).publish(any());
    }
}
