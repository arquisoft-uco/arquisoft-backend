package com.arquisoft.usuarios.application.bibliotecario.command.usecase.impl;

import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.bibliotecario.command.finder.BibliotecarioPorUsuarioFinder;
import com.arquisoft.usuarios.application.bibliotecario.command.secondaryport.BibliotecarioOutputPort;
import com.arquisoft.usuarios.application.bibliotecario.command.validator.RemoverBibliotecarioValidator;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.usuarios.domain.bibliotecario.event.BibliotecarioRemovidoEvent;
import com.arquisoft.usuarios.domain.bibliotecario.exception.BibliotecarioNoEncontradoException;
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
class RemoverBibliotecarioUseCaseImplTest {

    @Mock
    private BibliotecarioOutputPort bibliotecarioOutputPort;
    @Mock
    private ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    @Mock
    private BibliotecarioPorUsuarioFinder bibliotecarioPorUsuarioFinder;
    @Mock
    private UsuarioPorIdFinder usuarioPorIdFinder;
    @Mock
    private RemoverBibliotecarioValidator removerBibliotecarioValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private RemoverBibliotecarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverBibliotecarioUseCaseImpl(bibliotecarioOutputPort, proveedorIdentidadOutputPort,
                bibliotecarioPorUsuarioFinder, usuarioPorIdFinder, removerBibliotecarioValidator, eventPublisher,
                logger);
    }

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void debeEliminarRevocarYPublicarEnOrden_cuandoBibliotecarioEstaVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigente = BibliotecarioDomain.crear(id);
        var usuario = usuario(id);
        when(bibliotecarioPorUsuarioFinder.obtener(id)).thenReturn(vigente);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario);

        // Act
        useCase.ejecutar(BibliotecarioDomain.crear(id));

        // Assert
        verify(bibliotecarioPorUsuarioFinder, times(1)).obtener(id);
        verify(usuarioPorIdFinder, times(1)).obtener(id);

        var orden = inOrder(removerBibliotecarioValidator, bibliotecarioOutputPort, proveedorIdentidadOutputPort,
                eventPublisher);
        var captorInstante = ArgumentCaptor.forClass(Instant.class);
        var captorEvento = ArgumentCaptor.forClass(BibliotecarioRemovidoEvent.class);
        orden.verify(removerBibliotecarioValidator).validar(id, vigente);
        orden.verify(bibliotecarioOutputPort).eliminarLogica(eq(id), captorInstante.capture());
        orden.verify(proveedorIdentidadOutputPort).revocarRealmRole(id, UsuariosRealmRoles.BIBLIOTECARIO);
        orden.verify(eventPublisher).publish(captorEvento.capture());

        assertThat(captorInstante.getValue()).isNotEqualTo(UtilFecha.VACIO);
        var evento = captorEvento.getValue();
        assertThat(evento.getUsuario()).isEqualTo(id);
        assertThat(evento.getIdentificador()).isEqualTo(usuario.getIdentificador());
        assertThat(evento.getNombre()).isEqualTo(usuario.getNombre());
        assertThat(evento.getEmail()).isEqualTo(usuario.getEmail());
    }

    @Test
    void noDebeEscribirRevocarNiPublicar_cuandoLaReglaRechaza() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(bibliotecarioPorUsuarioFinder.obtener(id)).thenReturn(BibliotecarioDomain.VACIO);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(UsuarioDomain.VACIO);
        doThrow(new BibliotecarioNoEncontradoException(id))
                .when(removerBibliotecarioValidator).validar(id, BibliotecarioDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(BibliotecarioDomain.crear(id)))
                .isInstanceOf(BibliotecarioNoEncontradoException.class);
        verify(bibliotecarioOutputPort, never()).eliminarLogica(any(), any());
        verify(proveedorIdentidadOutputPort, never()).revocarRealmRole(any(), anyString());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePropagarYNoPublicar_cuandoProveedorIdentidadFalla() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(bibliotecarioPorUsuarioFinder.obtener(id)).thenReturn(BibliotecarioDomain.crear(id));
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario(id));
        var fallo = new InfrastructureException("Proveedor de identidad caido", "PROVEEDOR_NO_DISPONIBLE");
        doThrow(fallo).when(proveedorIdentidadOutputPort).revocarRealmRole(id, UsuariosRealmRoles.BIBLIOTECARIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(BibliotecarioDomain.crear(id))).isSameAs(fallo);
        verify(bibliotecarioOutputPort, times(1)).eliminarLogica(eq(id), any());
        verify(eventPublisher, never()).publish(any());
    }
}
