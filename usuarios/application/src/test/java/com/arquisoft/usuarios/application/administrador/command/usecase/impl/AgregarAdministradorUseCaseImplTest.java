package com.arquisoft.usuarios.application.administrador.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.administrador.command.finder.AdministradorPorUsuarioFinder;
import com.arquisoft.usuarios.application.administrador.command.secondaryport.AdministradorOutputPort;
import com.arquisoft.usuarios.application.administrador.command.secondaryport.entity.AdministradorEntity;
import com.arquisoft.usuarios.application.administrador.command.validator.AgregarAdministradorValidator;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import com.arquisoft.usuarios.domain.administrador.event.AdministradorAgregadoEvent;
import com.arquisoft.usuarios.domain.administrador.exception.AdministradorUsuarioDuplicadoException;
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
class AgregarAdministradorUseCaseImplTest {

    @Mock
    private AdministradorOutputPort administradorOutputPort;
    @Mock
    private AdministradorPorUsuarioFinder administradorPorUsuarioFinder;
    @Mock
    private AgregarAdministradorValidator agregarAdministradorValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private AgregarAdministradorUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarAdministradorUseCaseImpl(
                administradorOutputPort, administradorPorUsuarioFinder,
                agregarAdministradorValidator, eventPublisher, logger);
    }

    private UsuarioDomain usuario() {
        return UsuarioDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void debeGuardarYPublicarEnOrden_cuandoUsuarioNoEsAdministrador() {
        // Arrange
        var usuario = usuario();
        when(administradorPorUsuarioFinder.obtener(usuario.getId()))
                .thenReturn(AdministradorDomain.VACIO);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        var orden = inOrder(administradorPorUsuarioFinder, agregarAdministradorValidator,
                administradorOutputPort, eventPublisher);
        orden.verify(administradorPorUsuarioFinder).obtener(usuario.getId());
        orden.verify(agregarAdministradorValidator).validar(usuario.getId(), AdministradorDomain.VACIO);
        orden.verify(administradorOutputPort).guardar(any());
        orden.verify(eventPublisher).publish(any());

        var captorEntity = ArgumentCaptor.forClass(AdministradorEntity.class);
        verify(administradorOutputPort, times(1)).guardar(captorEntity.capture());
        assertThat(captorEntity.getValue().usuario()).isEqualTo(usuario.getId());
        assertThat(captorEntity.getValue().eliminadoEn()).isEqualTo(UtilFecha.VACIO);

        var captorEvento = ArgumentCaptor.forClass(AdministradorAgregadoEvent.class);
        verify(eventPublisher, times(1)).publish(captorEvento.capture());
        assertThat(captorEvento.getValue().getUsuario()).isEqualTo(usuario.getId());
        assertThat(captorEvento.getValue().getIdentificador()).isEqualTo(usuario.getIdentificador());
        assertThat(captorEvento.getValue().getNombre()).isEqualTo(usuario.getNombre());
        assertThat(captorEvento.getValue().getEmail()).isEqualTo(usuario.getEmail());

        verify(administradorPorUsuarioFinder, times(1)).obtener(usuario.getId());
        verify(administradorOutputPort, never()).reactivar(any());
    }

    @Test
    void debeReactivarYPublicarSinGuardar_cuandoElAdministradorEstaEliminado() {
        // Arrange
        var usuario = usuario();
        var eliminado = AdministradorDomain.reconstruir(usuario.getId(), Instant.parse("2026-09-24T10:00:00Z"));
        when(administradorPorUsuarioFinder.obtener(usuario.getId())).thenReturn(eliminado);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        var orden = inOrder(agregarAdministradorValidator, administradorOutputPort, eventPublisher);
        orden.verify(agregarAdministradorValidator).validar(usuario.getId(), eliminado);
        orden.verify(administradorOutputPort).reactivar(usuario.getId());
        orden.verify(eventPublisher).publish(any(AdministradorAgregadoEvent.class));
        verify(administradorOutputPort, never()).guardar(any());
        verify(administradorPorUsuarioFinder, times(1)).obtener(usuario.getId());
        assertThat(eliminado.estaEliminado()).isFalse();
    }

    @Test
    void debePropagarYNoEscribirNiPublicar_cuandoElUsuarioYaEsAdministradorVigente() {
        // Arrange
        var usuario = usuario();
        var vigente = AdministradorDomain.crear(usuario.getId());
        when(administradorPorUsuarioFinder.obtener(usuario.getId())).thenReturn(vigente);
        doThrow(new AdministradorUsuarioDuplicadoException(usuario.getId()))
                .when(agregarAdministradorValidator).validar(usuario.getId(), vigente);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(usuario))
                .isInstanceOf(AdministradorUsuarioDuplicadoException.class);
        verify(administradorOutputPort, never()).guardar(any());
        verify(administradorOutputPort, never()).reactivar(any());
        verify(eventPublisher, never()).publish(any());
    }
}
