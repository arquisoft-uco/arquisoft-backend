package com.arquisoft.usuarios.domain.estadousuario;

import com.arquisoft.usuarios.domain.estadousuario.exception.EstadoUsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoUsuarioTest {

    @Test
    void debeResolverActivo_cuandoIdEsActivo() {
        // Act
        var estado = EstadoUsuario.desde("ACTIVO");

        // Assert
        assertThat(estado).isEqualTo(EstadoUsuario.ACTIVO);
        assertThat(estado.getId()).isEqualTo("ACTIVO");
        assertThat(estado.getNombre()).isEqualTo("Activo");
    }

    @Test
    void debeLanzarExcepcion_cuandoIdNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoUsuario.desde("XXX"))
                .isInstanceOf(EstadoUsuarioNoEncontradoException.class);
    }
}
