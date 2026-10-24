package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.none;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.infrastructure.usuario.exception.ProveedorIdentidadUsuarioNoDisponibleException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProveedorIdentidadNuloOutputAdapterTest {

    private final ProveedorIdentidadNuloOutputAdapter adaptador = new ProveedorIdentidadNuloOutputAdapter();

    @Test
    void debeLanzar503_cuandoSeInvocaCualquierOperacion() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act / Assert
        assertThatThrownBy(() -> adaptador.existeEmail("a@uco.edu.co"))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
        assertThatThrownBy(() -> adaptador.existeEmailEnOtraIdentidad("a@uco.edu.co", usuario))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
        assertThatThrownBy(() -> adaptador.actualizar(null))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
        assertThatThrownBy(() -> adaptador.asignarRoles(usuario, List.of("ESTUDIANTE")))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
        assertThatThrownBy(() -> adaptador.registrar(null))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
        assertThatThrownBy(() -> adaptador.eliminar(usuario))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
        assertThatThrownBy(() -> adaptador.revocarRol(usuario, "ESTUDIANTE"))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
        assertThatThrownBy(() -> adaptador.cambiarHabilitacion(usuario, true))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
    }
}
