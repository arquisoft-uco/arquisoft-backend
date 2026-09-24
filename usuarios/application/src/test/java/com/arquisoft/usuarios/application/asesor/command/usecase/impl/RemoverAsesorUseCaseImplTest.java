package com.arquisoft.usuarios.application.asesor.command.usecase.impl;

import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.asesor.command.finder.AsesorPorUsuarioFinder;
import com.arquisoft.usuarios.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.usuarios.application.asesor.command.validator.RemoverAsesorValidator;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesor.event.AsesorRemovidoEvent;
import com.arquisoft.usuarios.domain.asesor.exception.AsesorNoEncontradoException;
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
class RemoverAsesorUseCaseImplTest {

    @Mock
    private AsesorOutputPort asesorOutputPort;
    @Mock
    private ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    @Mock
    private AsesorPorUsuarioFinder asesorPorUsuarioFinder;
    @Mock
    private UsuarioPorIdFinder usuarioPorIdFinder;
    @Mock
    private RemoverAsesorValidator removerAsesorValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private RemoverAsesorUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverAsesorUseCaseImpl(asesorOutputPort, proveedorIdentidadOutputPort,
                asesorPorUsuarioFinder, usuarioPorIdFinder, removerAsesorValidator, eventPublisher, logger);
    }

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "1036950123", "Carlos Rios", "carlos@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO);
    }

    @Test
    void debeEliminarRevocarYPublicarEnOrden_cuandoAsesorEstaVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigente = AsesorDomain.crear(id);
        var usuario = usuario(id);
        when(asesorPorUsuarioFinder.obtener(id)).thenReturn(vigente);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario);

        // Act
        useCase.ejecutar(AsesorDomain.crear(id));

        // Assert
        verify(asesorPorUsuarioFinder, times(1)).obtener(id);
        verify(usuarioPorIdFinder, times(1)).obtener(id);

        var orden = inOrder(removerAsesorValidator, asesorOutputPort, proveedorIdentidadOutputPort, eventPublisher);
        var captorInstante = ArgumentCaptor.forClass(Instant.class);
        var captorEvento = ArgumentCaptor.forClass(AsesorRemovidoEvent.class);
        orden.verify(removerAsesorValidator).validar(id, vigente);
        orden.verify(asesorOutputPort).eliminarLogica(eq(id), captorInstante.capture());
        orden.verify(proveedorIdentidadOutputPort).revocarRealmRole(id, UsuariosRealmRoles.ASESOR);
        orden.verify(eventPublisher).publish(captorEvento.capture());

        assertThat(captorInstante.getValue()).isNotEqualTo(UtilFecha.VACIO);
        var evento = captorEvento.getValue();
        assertThat(evento.getUsuario()).isEqualTo(id);
        assertThat(evento.getIdentificador()).isEqualTo(usuario.getIdentificador());
        assertThat(evento.getNombre()).isEqualTo(usuario.getNombre());
        assertThat(evento.getEmail()).isEqualTo(usuario.getEmail());
    }

    @Test
    void noDebeEscribirRevocarNiPublicar_cuandoAsesorNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(asesorPorUsuarioFinder.obtener(id)).thenReturn(AsesorDomain.VACIO);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(UsuarioDomain.VACIO);
        doThrow(new AsesorNoEncontradoException(id)).when(removerAsesorValidator).validar(id, AsesorDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(AsesorDomain.crear(id)))
                .isInstanceOf(AsesorNoEncontradoException.class);
        verify(asesorOutputPort, never()).eliminarLogica(any(), any());
        verify(proveedorIdentidadOutputPort, never()).revocarRealmRole(any(), anyString());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void noDebeEscribirRevocarNiPublicar_cuandoAsesorYaFueRemovido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var removido = AsesorDomain.reconstruir(id, Instant.parse("2026-09-01T10:00:00Z"));
        when(asesorPorUsuarioFinder.obtener(id)).thenReturn(removido);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario(id));
        doThrow(new AsesorNoEncontradoException(id)).when(removerAsesorValidator).validar(id, removido);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(AsesorDomain.crear(id)))
                .isInstanceOf(AsesorNoEncontradoException.class);
        verify(asesorOutputPort, never()).eliminarLogica(any(), any());
        verify(proveedorIdentidadOutputPort, never()).revocarRealmRole(any(), anyString());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePropagarYNoPublicar_cuandoProveedorIdentidadFalla() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(asesorPorUsuarioFinder.obtener(id)).thenReturn(AsesorDomain.crear(id));
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario(id));
        var fallo = new InfrastructureException("Proveedor de identidad caido", "PROVEEDOR_NO_DISPONIBLE");
        doThrow(fallo).when(proveedorIdentidadOutputPort).revocarRealmRole(id, UsuariosRealmRoles.ASESOR);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(AsesorDomain.crear(id))).isSameAs(fallo);
        verify(asesorOutputPort, times(1)).eliminarLogica(eq(id), any());
        verify(eventPublisher, never()).publish(any());
    }
}
