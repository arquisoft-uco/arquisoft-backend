package com.arquisoft.usuarios.application.usuario.command.usecase.impl;

import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.validator.CambiarEstadoUsuarioValidator;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.CambioEstadoUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.event.UsuarioEstadoCambiadoEvent;
import com.arquisoft.usuarios.domain.usuario.exception.EstadoUsuarioSinCambioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoUsuarioUseCaseImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;
    @Mock
    private ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    @Mock
    private UsuarioPorIdFinder usuarioPorIdFinder;
    @Mock
    private CambiarEstadoUsuarioValidator cambiarEstadoUsuarioValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private CambiarEstadoUsuarioUseCaseImpl useCase;

    private static UsuarioDomain usuario(UUID id, EstadoUsuario estado, Instant eliminadoEn) {
        return UsuarioDomain.reconstruir(id, "1000000001", "Ana Perez", "ana@uco.edu.co", "3000000001",
                estado, eliminadoEn);
    }

    @Test
    void debeValidarPersistirDeshabilitarYPublicarEnOrden_cuandoSeInactivaUnUsuarioActivo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var encontrado = usuario(id, EstadoUsuario.ACTIVO, UtilFecha.VACIO);
        var cambio = CambioEstadoUsuarioDomain.crear(id, EstadoUsuario.INACTIVO.getId());
        when(usuarioPorIdFinder.obtener(id)).thenReturn(encontrado);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(usuarioPorIdFinder, cambiarEstadoUsuarioValidator, usuarioOutputPort,
                proveedorIdentidadOutputPort, eventPublisher);
        orden.verify(usuarioPorIdFinder).obtener(id);
        orden.verify(cambiarEstadoUsuarioValidator).validar(cambio, encontrado);
        orden.verify(usuarioOutputPort).cambiarEstado(id, EstadoUsuario.INACTIVO.getId(), UtilFecha.VACIO);
        orden.verify(proveedorIdentidadOutputPort).cambiarHabilitacion(id, false);
        var captor = ArgumentCaptor.forClass(UsuarioEstadoCambiadoEvent.class);
        orden.verify(eventPublisher).publish(captor.capture());
        verify(usuarioPorIdFinder, times(1)).obtener(any());

        var evento = captor.getValue();
        assertThat(evento.getUsuario()).isEqualTo(id);
        assertThat(evento.getNombre()).isEqualTo("Ana Perez");
        assertThat(evento.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getEstado()).isEqualTo(EstadoUsuario.INACTIVO.getId());
        assertThat(evento.getEstadoNombre()).isEqualTo("Inactivo");
    }

    @Test
    void debeRestaurarHabilitarYPublicarActivo_cuandoSeActivaUnUsuarioEliminado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(usuarioPorIdFinder.obtener(id))
                .thenReturn(usuario(id, EstadoUsuario.INACTIVO, Instant.parse("2026-09-01T10:00:00Z")));

        // Act
        useCase.ejecutar(CambioEstadoUsuarioDomain.crear(id, EstadoUsuario.ACTIVO.getId()));

        // Assert
        verify(usuarioOutputPort).cambiarEstado(id, EstadoUsuario.ACTIVO.getId(), UtilFecha.VACIO);
        verify(proveedorIdentidadOutputPort).cambiarHabilitacion(id, true);
        var captor = ArgumentCaptor.forClass(UsuarioEstadoCambiadoEvent.class);
        verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoUsuario.ACTIVO.getId());
        assertThat(captor.getValue().getEstadoNombre()).isEqualTo("Activo");
    }

    @Test
    void debeConservarEliminadoEn_cuandoSeInactivaUnUsuarioRecienEliminadoEnCadena() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-09-26T08:00:00Z");
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario(id, EstadoUsuario.ACTIVO, eliminadoEn));

        // Act
        useCase.ejecutar(CambioEstadoUsuarioDomain.crear(id, EstadoUsuario.INACTIVO.getId()));

        // Assert
        verify(usuarioOutputPort).cambiarEstado(id, EstadoUsuario.INACTIVO.getId(), eliminadoEn);
        verify(proveedorIdentidadOutputPort).cambiarHabilitacion(id, false);
    }

    @Test
    void debeLanzarSinPersistirNiNotificar_cuandoElValidatorRechazaElCambio() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var encontrado = usuario(id, EstadoUsuario.ACTIVO, UtilFecha.VACIO);
        var cambio = CambioEstadoUsuarioDomain.crear(id, EstadoUsuario.ACTIVO.getId());
        when(usuarioPorIdFinder.obtener(id)).thenReturn(encontrado);
        doThrow(new EstadoUsuarioSinCambioException(id, "Activo"))
                .when(cambiarEstadoUsuarioValidator).validar(cambio, encontrado);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio)).isInstanceOf(EstadoUsuarioSinCambioException.class);
        verify(usuarioOutputPort, never()).cambiarEstado(any(), any(), any());
        verify(proveedorIdentidadOutputPort, never()).cambiarHabilitacion(any(), anyBoolean());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void debePropagarElFalloSinPublicar_cuandoElProveedorDeIdentidadNoResponde() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario(id, EstadoUsuario.ACTIVO, UtilFecha.VACIO));
        var fallo = new InfrastructureException("Proveedor de identidad caido", "PROVEEDOR_NO_DISPONIBLE");
        doThrow(fallo).when(proveedorIdentidadOutputPort).cambiarHabilitacion(id, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(
                CambioEstadoUsuarioDomain.crear(id, EstadoUsuario.INACTIVO.getId())))
                .isSameAs(fallo);
        verifyNoInteractions(eventPublisher);
    }
}
