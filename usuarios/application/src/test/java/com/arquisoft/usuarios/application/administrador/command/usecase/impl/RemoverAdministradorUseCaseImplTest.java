package com.arquisoft.usuarios.application.administrador.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.administrador.command.finder.AdministradorPorUsuarioFinder;
import com.arquisoft.usuarios.application.administrador.command.finder.AdministradoresVigentesCountFinder;
import com.arquisoft.usuarios.application.administrador.command.secondaryport.AdministradorOutputPort;
import com.arquisoft.usuarios.application.administrador.command.validator.RemoverAdministradorValidator;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import com.arquisoft.usuarios.domain.administrador.RemocionAdministradorDomain;
import com.arquisoft.usuarios.domain.administrador.event.AdministradorRemovidoEvent;
import com.arquisoft.usuarios.domain.administrador.exception.AdministradorUnicoVigenteException;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverAdministradorUseCaseImplTest {

    @Mock
    private AdministradorOutputPort administradorOutputPort;
    @Mock
    private ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    @Mock
    private AdministradorPorUsuarioFinder administradorPorUsuarioFinder;
    @Mock
    private UsuarioPorIdFinder usuarioPorIdFinder;
    @Mock
    private AdministradoresVigentesCountFinder administradoresVigentesCountFinder;
    @Mock
    private RemoverAdministradorValidator removerAdministradorValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private RemoverAdministradorUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverAdministradorUseCaseImpl(administradorOutputPort, proveedorIdentidadOutputPort,
                administradorPorUsuarioFinder, usuarioPorIdFinder, administradoresVigentesCountFinder,
                removerAdministradorValidator, eventPublisher, logger);
    }

    private static UsuarioDomain usuario(java.util.UUID id) {
        return UsuarioDomain.reconstruir(id, "usr001", "Ana Perez", "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void debeRemoverRevocarYPublicar_cuandoLaValidacionPasa() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var usuarioId = UtilUUID.generarNuevoUUID();
        var entrada = RemocionAdministradorDomain.crear(usuarioId, actor);
        var administrador = AdministradorDomain.crear(usuarioId);
        var usuario = usuario(usuarioId);
        when(administradorPorUsuarioFinder.obtener(usuarioId)).thenReturn(administrador);
        when(usuarioPorIdFinder.obtener(usuarioId)).thenReturn(usuario);
        when(administradoresVigentesCountFinder.obtener()).thenReturn(2L);

        // Act
        useCase.ejecutar(entrada);

        // Assert
        verify(administradorPorUsuarioFinder, times(1)).obtener(usuarioId);
        verify(usuarioPorIdFinder, times(1)).obtener(usuarioId);
        verify(administradoresVigentesCountFinder, times(1)).obtener();

        var orden = inOrder(removerAdministradorValidator, administradorOutputPort,
                proveedorIdentidadOutputPort, eventPublisher);
        orden.verify(removerAdministradorValidator).validar(actor, usuarioId, administrador, 2L);
        orden.verify(administradorOutputPort).eliminarLogica(eq(usuarioId), any());
        orden.verify(proveedorIdentidadOutputPort).revocarRealmRole(usuarioId, UsuariosRealmRoles.ADMINISTRADOR);

        var captorEvento = ArgumentCaptor.forClass(AdministradorRemovidoEvent.class);
        orden.verify(eventPublisher).publish(captorEvento.capture());

        assertThat(administrador.estaEliminado()).isTrue();
        assertThat(captorEvento.getValue().getUsuario()).isEqualTo(usuarioId);
        assertThat(captorEvento.getValue().getIdentificador()).isEqualTo(usuario.getIdentificador());
        assertThat(captorEvento.getValue().getNombre()).isEqualTo(usuario.getNombre());
        assertThat(captorEvento.getValue().getEmail()).isEqualTo(usuario.getEmail());
    }

    @Test
    void noDebePersistirNiRevocarNiPublicar_cuandoElValidatorLanza() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var usuarioId = UtilUUID.generarNuevoUUID();
        var entrada = RemocionAdministradorDomain.crear(usuarioId, actor);
        var administrador = AdministradorDomain.crear(usuarioId);
        when(administradorPorUsuarioFinder.obtener(usuarioId)).thenReturn(administrador);
        when(usuarioPorIdFinder.obtener(usuarioId)).thenReturn(usuario(usuarioId));
        when(administradoresVigentesCountFinder.obtener()).thenReturn(1L);
        var rechazo = new AdministradorUnicoVigenteException(usuarioId);
        doThrow(rechazo).when(removerAdministradorValidator).validar(actor, usuarioId, administrador, 1L);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada)).isSameAs(rechazo);
        verify(administradorOutputPort, never()).eliminarLogica(any(), any());
        verifyNoInteractions(proveedorIdentidadOutputPort, eventPublisher);
    }
}
