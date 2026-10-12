package com.arquisoft.usuarios.application.bibliotecario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.bibliotecario.command.finder.BibliotecarioPorUsuarioFinder;
import com.arquisoft.usuarios.application.bibliotecario.command.secondaryport.BibliotecarioOutputPort;
import com.arquisoft.usuarios.application.bibliotecario.command.secondaryport.entity.BibliotecarioEntity;
import com.arquisoft.usuarios.application.bibliotecario.command.validator.AgregarBibliotecarioValidator;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.usuarios.domain.bibliotecario.event.BibliotecarioAgregadoEvent;
import com.arquisoft.usuarios.domain.bibliotecario.exception.BibliotecarioUsuarioDuplicadoException;
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
class AgregarBibliotecarioUseCaseImplTest {

    @Mock
    private BibliotecarioOutputPort bibliotecarioOutputPort;
    @Mock
    private BibliotecarioPorUsuarioFinder bibliotecarioPorUsuarioFinder;
    @Mock
    private AgregarBibliotecarioValidator agregarBibliotecarioValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private AgregarBibliotecarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarBibliotecarioUseCaseImpl(
                bibliotecarioOutputPort, bibliotecarioPorUsuarioFinder,
                agregarBibliotecarioValidator, eventPublisher, logger);
    }

    private UsuarioDomain usuario() {
        return UsuarioDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void debeGuardarYPublicarEnOrden_cuandoUsuarioNoEsBibliotecario() {
        // Arrange
        var usuario = usuario();
        when(bibliotecarioPorUsuarioFinder.obtener(usuario.getId())).thenReturn(BibliotecarioDomain.VACIO);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        var orden = inOrder(bibliotecarioPorUsuarioFinder, agregarBibliotecarioValidator,
                bibliotecarioOutputPort, eventPublisher);
        orden.verify(bibliotecarioPorUsuarioFinder).obtener(usuario.getId());
        orden.verify(agregarBibliotecarioValidator).validar(usuario.getId(), BibliotecarioDomain.VACIO);
        orden.verify(bibliotecarioOutputPort).guardar(any());
        orden.verify(eventPublisher).publish(any());

        var captorEntity = ArgumentCaptor.forClass(BibliotecarioEntity.class);
        verify(bibliotecarioOutputPort, times(1)).guardar(captorEntity.capture());
        assertThat(captorEntity.getValue().usuario()).isEqualTo(usuario.getId());
        assertThat(captorEntity.getValue().eliminadoEn()).isEqualTo(UtilFecha.VACIO);

        var captorEvento = ArgumentCaptor.forClass(BibliotecarioAgregadoEvent.class);
        verify(eventPublisher, times(1)).publish(captorEvento.capture());
        assertThat(captorEvento.getValue().getUsuario()).isEqualTo(usuario.getId());
        assertThat(captorEvento.getValue().getIdentificador()).isEqualTo(usuario.getIdentificador());
        assertThat(captorEvento.getValue().getNombre()).isEqualTo(usuario.getNombre());
        assertThat(captorEvento.getValue().getEmail()).isEqualTo(usuario.getEmail());

        verify(bibliotecarioPorUsuarioFinder, times(1)).obtener(usuario.getId());
        verify(bibliotecarioOutputPort, never()).reactivar(any());
    }

    @Test
    void debeReactivarYPublicarSinGuardar_cuandoElBibliotecarioEstaEliminado() {
        // Arrange
        var usuario = usuario();
        var eliminado = BibliotecarioDomain.reconstruir(usuario.getId(), Instant.parse("2026-09-24T10:00:00Z"));
        when(bibliotecarioPorUsuarioFinder.obtener(usuario.getId())).thenReturn(eliminado);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        var orden = inOrder(agregarBibliotecarioValidator, bibliotecarioOutputPort, eventPublisher);
        orden.verify(agregarBibliotecarioValidator).validar(usuario.getId(), eliminado);
        orden.verify(bibliotecarioOutputPort).reactivar(usuario.getId());
        orden.verify(eventPublisher).publish(any(BibliotecarioAgregadoEvent.class));
        verify(bibliotecarioOutputPort, never()).guardar(any());
        verify(bibliotecarioPorUsuarioFinder, times(1)).obtener(usuario.getId());
        assertThat(eliminado.estaEliminado()).isFalse();
    }

    @Test
    void debePropagarYNoEscribirNiPublicar_cuandoElUsuarioYaEsBibliotecarioVigente() {
        // Arrange
        var usuario = usuario();
        var vigente = BibliotecarioDomain.crear(usuario.getId());
        when(bibliotecarioPorUsuarioFinder.obtener(usuario.getId())).thenReturn(vigente);
        doThrow(new BibliotecarioUsuarioDuplicadoException(usuario.getId()))
                .when(agregarBibliotecarioValidator).validar(usuario.getId(), vigente);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(usuario))
                .isInstanceOf(BibliotecarioUsuarioDuplicadoException.class);
        verify(bibliotecarioOutputPort, never()).guardar(any());
        verify(bibliotecarioOutputPort, never()).reactivar(any());
        verify(eventPublisher, never()).publish(any());
    }
}
