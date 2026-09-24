package com.arquisoft.usuarios.application.estudiante.command.usecase.impl;

import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.estudiante.command.finder.EstudiantePorUsuarioFinder;
import com.arquisoft.usuarios.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.usuarios.application.estudiante.command.validator.RemoverEstudianteValidator;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.estudiante.event.EstudianteRemovidoEvent;
import com.arquisoft.usuarios.domain.estudiante.exception.EstudianteNoEncontradoException;
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
class RemoverEstudianteUseCaseImplTest {

    @Mock
    private EstudianteOutputPort estudianteOutputPort;
    @Mock
    private ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    @Mock
    private EstudiantePorUsuarioFinder estudiantePorUsuarioFinder;
    @Mock
    private UsuarioPorIdFinder usuarioPorIdFinder;
    @Mock
    private RemoverEstudianteValidator removerEstudianteValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private RemoverEstudianteUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverEstudianteUseCaseImpl(estudianteOutputPort, proveedorIdentidadOutputPort,
                estudiantePorUsuarioFinder, usuarioPorIdFinder, removerEstudianteValidator, eventPublisher, logger);
    }

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO);
    }

    @Test
    void debeEliminarRevocarYPublicarEnOrden_cuandoEstudianteEstaVigente() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = EstudianteDomain.crear(id);
        var usuario = usuario(id);
        when(estudiantePorUsuarioFinder.obtener(id)).thenReturn(vigente);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario);

        // Act
        useCase.ejecutar(EstudianteDomain.crear(id));

        // Assert
        verify(estudiantePorUsuarioFinder, times(1)).obtener(id);
        verify(usuarioPorIdFinder, times(1)).obtener(id);

        var orden = inOrder(removerEstudianteValidator, estudianteOutputPort, proveedorIdentidadOutputPort,
                eventPublisher);
        var captorInstante = ArgumentCaptor.forClass(Instant.class);
        var captorEvento = ArgumentCaptor.forClass(EstudianteRemovidoEvent.class);
        orden.verify(removerEstudianteValidator).validar(id, vigente);
        orden.verify(estudianteOutputPort).eliminarLogica(eq(id), captorInstante.capture());
        orden.verify(proveedorIdentidadOutputPort).revocarRealmRole(id, UsuariosRealmRoles.ESTUDIANTE);
        orden.verify(eventPublisher).publish(captorEvento.capture());

        assertThat(captorInstante.getValue()).isNotEqualTo(UtilFecha.VACIO);
        var evento = captorEvento.getValue();
        assertThat(evento.getUsuario()).isEqualTo(id);
        assertThat(evento.getIdentificador()).isEqualTo(usuario.getIdentificador());
        assertThat(evento.getNombre()).isEqualTo(usuario.getNombre());
        assertThat(evento.getEmail()).isEqualTo(usuario.getEmail());
    }

    @Test
    void noDebeEscribirRevocarNiPublicar_cuandoEstudianteNoExiste() {
        // Arrange
        var id = UUID.randomUUID();
        when(estudiantePorUsuarioFinder.obtener(id)).thenReturn(EstudianteDomain.VACIO);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(UsuarioDomain.VACIO);
        doThrow(new EstudianteNoEncontradoException(id))
                .when(removerEstudianteValidator).validar(id, EstudianteDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(EstudianteDomain.crear(id)))
                .isInstanceOf(EstudianteNoEncontradoException.class);
        verify(estudianteOutputPort, never()).eliminarLogica(any(), any());
        verify(proveedorIdentidadOutputPort, never()).revocarRealmRole(any(), anyString());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void noDebeEscribirRevocarNiPublicar_cuandoEstudianteYaFueRemovido() {
        // Arrange
        var id = UUID.randomUUID();
        var removido = EstudianteDomain.reconstruir(id, Instant.parse("2026-09-01T10:00:00Z"));
        when(estudiantePorUsuarioFinder.obtener(id)).thenReturn(removido);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario(id));
        doThrow(new EstudianteNoEncontradoException(id)).when(removerEstudianteValidator).validar(id, removido);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(EstudianteDomain.crear(id)))
                .isInstanceOf(EstudianteNoEncontradoException.class);
        verify(estudianteOutputPort, never()).eliminarLogica(any(), any());
        verify(proveedorIdentidadOutputPort, never()).revocarRealmRole(any(), anyString());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePropagarYNoPublicar_cuandoProveedorIdentidadFalla() {
        // Arrange
        var id = UUID.randomUUID();
        when(estudiantePorUsuarioFinder.obtener(id)).thenReturn(EstudianteDomain.crear(id));
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario(id));
        var fallo = new InfrastructureException("Proveedor de identidad caido", "PROVEEDOR_NO_DISPONIBLE");
        doThrow(fallo).when(proveedorIdentidadOutputPort).revocarRealmRole(id, UsuariosRealmRoles.ESTUDIANTE);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(EstudianteDomain.crear(id))).isSameAs(fallo);
        verify(estudianteOutputPort, times(1)).eliminarLogica(eq(id), any());
        verify(eventPublisher, never()).publish(any());
    }
}
