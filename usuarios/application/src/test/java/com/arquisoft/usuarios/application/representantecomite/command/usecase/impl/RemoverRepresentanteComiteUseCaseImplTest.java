package com.arquisoft.usuarios.application.representantecomite.command.usecase.impl;

import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.representantecomite.command.finder.RepresentanteComitePorUsuarioFinder;
import com.arquisoft.usuarios.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.usuarios.application.representantecomite.command.validator.RemoverRepresentanteComiteValidator;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.representantecomite.event.RepresentanteComiteRemovidoEvent;
import com.arquisoft.usuarios.domain.representantecomite.exception.RepresentanteComiteNoEncontradoException;
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
class RemoverRepresentanteComiteUseCaseImplTest {

    @Mock
    private RepresentanteComiteOutputPort representanteComiteOutputPort;
    @Mock
    private ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    @Mock
    private RepresentanteComitePorUsuarioFinder representanteComitePorUsuarioFinder;
    @Mock
    private UsuarioPorIdFinder usuarioPorIdFinder;
    @Mock
    private RemoverRepresentanteComiteValidator removerRepresentanteComiteValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private RemoverRepresentanteComiteUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverRepresentanteComiteUseCaseImpl(representanteComiteOutputPort, proveedorIdentidadOutputPort,
                representanteComitePorUsuarioFinder, usuarioPorIdFinder, removerRepresentanteComiteValidator,
                eventPublisher, logger);
    }

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void debeEliminarRevocarYPublicarEnOrden_cuandoRepresentanteComiteEstaVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigente = RepresentanteComiteDomain.crear(id);
        var usuario = usuario(id);
        when(representanteComitePorUsuarioFinder.obtener(id)).thenReturn(vigente);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario);

        // Act
        useCase.ejecutar(RepresentanteComiteDomain.crear(id));

        // Assert
        verify(representanteComitePorUsuarioFinder, times(1)).obtener(id);
        verify(usuarioPorIdFinder, times(1)).obtener(id);

        var orden = inOrder(removerRepresentanteComiteValidator, representanteComiteOutputPort,
                proveedorIdentidadOutputPort, eventPublisher);
        var captorInstante = ArgumentCaptor.forClass(Instant.class);
        var captorEvento = ArgumentCaptor.forClass(RepresentanteComiteRemovidoEvent.class);
        orden.verify(removerRepresentanteComiteValidator).validar(id, vigente);
        orden.verify(representanteComiteOutputPort).eliminarLogica(eq(id), captorInstante.capture());
        orden.verify(proveedorIdentidadOutputPort).revocarRealmRole(id, UsuariosRealmRoles.REPRESENTANTE_COMITE);
        orden.verify(eventPublisher).publish(captorEvento.capture());

        assertThat(captorInstante.getValue()).isNotEqualTo(UtilFecha.VACIO);
        var evento = captorEvento.getValue();
        assertThat(evento.getUsuario()).isEqualTo(id);
        assertThat(evento.getIdentificador()).isEqualTo(usuario.getIdentificador());
        assertThat(evento.getNombre()).isEqualTo(usuario.getNombre());
        assertThat(evento.getEmail()).isEqualTo(usuario.getEmail());
    }

    @Test
    void noDebeEscribirRevocarNiPublicar_cuandoRepresentanteComiteNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(representanteComitePorUsuarioFinder.obtener(id)).thenReturn(RepresentanteComiteDomain.VACIO);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(UsuarioDomain.VACIO);
        doThrow(new RepresentanteComiteNoEncontradoException(id))
                .when(removerRepresentanteComiteValidator).validar(id, RepresentanteComiteDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(RepresentanteComiteDomain.crear(id)))
                .isInstanceOf(RepresentanteComiteNoEncontradoException.class);
        verify(representanteComiteOutputPort, never()).eliminarLogica(any(), any());
        verify(proveedorIdentidadOutputPort, never()).revocarRealmRole(any(), anyString());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void noDebeEscribirRevocarNiPublicar_cuandoRepresentanteComiteYaFueRemovido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var removido = RepresentanteComiteDomain.reconstruir(id, Instant.parse("2026-09-01T10:00:00Z"));
        when(representanteComitePorUsuarioFinder.obtener(id)).thenReturn(removido);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario(id));
        doThrow(new RepresentanteComiteNoEncontradoException(id))
                .when(removerRepresentanteComiteValidator).validar(id, removido);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(RepresentanteComiteDomain.crear(id)))
                .isInstanceOf(RepresentanteComiteNoEncontradoException.class);
        verify(representanteComiteOutputPort, never()).eliminarLogica(any(), any());
        verify(proveedorIdentidadOutputPort, never()).revocarRealmRole(any(), anyString());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePropagarYNoPublicar_cuandoProveedorIdentidadFalla() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(representanteComitePorUsuarioFinder.obtener(id)).thenReturn(RepresentanteComiteDomain.crear(id));
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario(id));
        var fallo = new InfrastructureException("Proveedor de identidad caido", "PROVEEDOR_NO_DISPONIBLE");
        doThrow(fallo).when(proveedorIdentidadOutputPort)
                .revocarRealmRole(id, UsuariosRealmRoles.REPRESENTANTE_COMITE);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(RepresentanteComiteDomain.crear(id))).isSameAs(fallo);
        verify(representanteComiteOutputPort, times(1)).eliminarLogica(eq(id), any());
        verify(eventPublisher, never()).publish(any());
    }
}
