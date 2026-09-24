package com.arquisoft.usuarios.application.coordinador.command.usecase.impl;

import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.coordinador.command.finder.CoordinadorPorUsuarioFinder;
import com.arquisoft.usuarios.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.usuarios.application.coordinador.command.validator.RemoverCoordinadorValidator;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.coordinador.event.CoordinadorRemovidoEvent;
import com.arquisoft.usuarios.domain.coordinador.exception.CoordinadorNoEncontradoException;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverCoordinadorUseCaseImplTest {

    @Mock
    private CoordinadorOutputPort coordinadorOutputPort;
    @Mock
    private ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    @Mock
    private CoordinadorPorUsuarioFinder coordinadorPorUsuarioFinder;
    @Mock
    private UsuarioPorIdFinder usuarioPorIdFinder;
    @Mock
    private RemoverCoordinadorValidator removerCoordinadorValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private RemoverCoordinadorUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverCoordinadorUseCaseImpl(coordinadorOutputPort, proveedorIdentidadOutputPort,
                coordinadorPorUsuarioFinder, usuarioPorIdFinder, removerCoordinadorValidator, eventPublisher, logger);
    }

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO);
    }

    @Test
    void debeEliminarRevocarYPublicarEnOrden_cuandoCoordinadorEstaVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigente = CoordinadorDomain.crear(id);
        var usuario = usuario(id);
        when(coordinadorPorUsuarioFinder.obtener(id)).thenReturn(vigente);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario);

        // Act
        useCase.ejecutar(CoordinadorDomain.crear(id));

        // Assert
        verify(coordinadorPorUsuarioFinder, times(1)).obtener(id);
        verify(usuarioPorIdFinder, times(1)).obtener(id);

        var orden = inOrder(removerCoordinadorValidator, coordinadorOutputPort, proveedorIdentidadOutputPort,
                eventPublisher);
        var captorInstante = ArgumentCaptor.forClass(Instant.class);
        var captorEvento = ArgumentCaptor.forClass(CoordinadorRemovidoEvent.class);
        orden.verify(removerCoordinadorValidator).validar(id, vigente);
        orden.verify(coordinadorOutputPort).eliminarLogica(eq(id), captorInstante.capture());
        orden.verify(proveedorIdentidadOutputPort).revocarRealmRole(id, UsuariosRealmRoles.COORDINADOR);
        orden.verify(eventPublisher).publish(captorEvento.capture());

        assertThat(captorInstante.getValue()).isNotEqualTo(UtilFecha.VACIO);
        var evento = captorEvento.getValue();
        assertThat(evento.getUsuario()).isEqualTo(id);
        assertThat(evento.getIdentificador()).isEqualTo(usuario.getIdentificador());
        assertThat(evento.getNombre()).isEqualTo(usuario.getNombre());
        assertThat(evento.getEmail()).isEqualTo(usuario.getEmail());
    }

    @Test
    void noDebeEscribirRevocarNiPublicar_cuandoCoordinadorNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(coordinadorPorUsuarioFinder.obtener(id)).thenReturn(CoordinadorDomain.VACIO);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(UsuarioDomain.VACIO);
        doThrow(new CoordinadorNoEncontradoException(id))
                .when(removerCoordinadorValidator).validar(id, CoordinadorDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(CoordinadorDomain.crear(id)))
                .isInstanceOf(CoordinadorNoEncontradoException.class);
        verify(coordinadorOutputPort, never()).eliminarLogica(any(), any());
        verify(proveedorIdentidadOutputPort, never()).revocarRealmRole(any(), anyString());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void noDebeEscribirRevocarNiPublicar_cuandoCoordinadorYaFueRemovido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var removido = CoordinadorDomain.reconstruir(id, Instant.parse("2026-09-01T10:00:00Z"));
        when(coordinadorPorUsuarioFinder.obtener(id)).thenReturn(removido);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario(id));
        doThrow(new CoordinadorNoEncontradoException(id)).when(removerCoordinadorValidator).validar(id, removido);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(CoordinadorDomain.crear(id)))
                .isInstanceOf(CoordinadorNoEncontradoException.class);
        verify(coordinadorOutputPort, never()).eliminarLogica(any(), any());
        verify(proveedorIdentidadOutputPort, never()).revocarRealmRole(any(), anyString());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePropagarYNoPublicar_cuandoProveedorIdentidadFalla() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(coordinadorPorUsuarioFinder.obtener(id)).thenReturn(CoordinadorDomain.crear(id));
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario(id));
        var fallo = new InfrastructureException("Proveedor de identidad caido", "PROVEEDOR_NO_DISPONIBLE");
        doThrow(fallo).when(proveedorIdentidadOutputPort).revocarRealmRole(id, UsuariosRealmRoles.COORDINADOR);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(CoordinadorDomain.crear(id))).isSameAs(fallo);
        verify(coordinadorOutputPort, times(1)).eliminarLogica(eq(id), any());
        verify(eventPublisher, never()).publish(any());
    }
}
