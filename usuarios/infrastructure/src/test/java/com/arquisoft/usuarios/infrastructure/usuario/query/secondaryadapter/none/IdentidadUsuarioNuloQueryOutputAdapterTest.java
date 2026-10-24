package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.none;

import com.arquisoft.usuarios.infrastructure.usuario.exception.ProveedorIdentidadUsuarioNoDisponibleException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdentidadUsuarioNuloQueryOutputAdapterTest {

    @Test
    void debeLanzar503_cuandoConsulta() {
        // Act / Assert
        assertThatThrownBy(() -> new IdentidadUsuarioNuloQueryOutputAdapter().consultar(null))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
    }
}
