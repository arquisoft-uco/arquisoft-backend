package com.arquisoft.usuarios.application.usuario.command.usecase.impl;

import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.CambioEstadoUsuarioDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoUsuarioUseCaseImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;
    @Mock
    private ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    @Mock
    private AppLogger logger;

    private CambiarEstadoUsuarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CambiarEstadoUsuarioUseCaseImpl(usuarioOutputPort, proveedorIdentidadOutputPort, logger);
    }

    @Test
    void debePersistirYDeshabilitarEnOrden_cuandoElEstadoEsInactivo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        useCase.ejecutar(CambioEstadoUsuarioDomain.crear(id, EstadoUsuario.INACTIVO));

        // Assert
        var orden = inOrder(usuarioOutputPort, proveedorIdentidadOutputPort);
        orden.verify(usuarioOutputPort).cambiarEstado(id, EstadoUsuario.INACTIVO.getId());
        orden.verify(proveedorIdentidadOutputPort).cambiarHabilitacion(id, false);
    }

    @Test
    void debePersistirYHabilitar_cuandoElEstadoEsActivo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        useCase.ejecutar(CambioEstadoUsuarioDomain.crear(id, EstadoUsuario.ACTIVO));

        // Assert
        verify(usuarioOutputPort).cambiarEstado(id, EstadoUsuario.ACTIVO.getId());
        verify(proveedorIdentidadOutputPort).cambiarHabilitacion(id, true);
    }

    @Test
    void debePropagarElFallo_cuandoElProveedorDeIdentidadNoResponde() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var fallo = new InfrastructureException("Proveedor de identidad caido", "PROVEEDOR_NO_DISPONIBLE");
        doThrow(fallo).when(proveedorIdentidadOutputPort).cambiarHabilitacion(id, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(CambioEstadoUsuarioDomain.crear(id, EstadoUsuario.INACTIVO)))
                .isSameAs(fallo);
    }
}
