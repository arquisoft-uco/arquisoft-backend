package com.arquisoft.seguridad.domain.usuario.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioRoleTest {

    @Test
    void debeRetornarCodigoCorrecto_cuandoRolEstudiante() {
        assertThat(UsuarioRole.ESTUDIANTE.getCode()).isEqualTo("estudiante");
    }

    @Test
    void debeRetornarCodigoCorrecto_cuandoRolAsesorFicha() {
        assertThat(UsuarioRole.ASESOR_FICHA.getCode()).isEqualTo("asesor-ficha");
    }

    @Test
    void debeEncontrarRol_cuandoCodigoAsesorFichaExiste() {
        String codigo = "asesor-ficha";

        UsuarioRole rol = UsuarioRole.fromCode(codigo);

        assertThat(rol).isEqualTo(UsuarioRole.ASESOR_FICHA);
    }

    @Test
    void debeLanzarExcepcion_cuandoCodigoInexistente() {
        String codigoInexistente = "admin";

        assertThatThrownBy(() -> UsuarioRole.fromCode(codigoInexistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("admin");
    }

    @Test
    void debeTener8Roles_cuandoSeListanTodos() {
        assertThat(UsuarioRole.values()).hasSize(8);
    }
}
