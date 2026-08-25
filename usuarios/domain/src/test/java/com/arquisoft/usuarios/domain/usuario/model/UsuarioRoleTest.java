package com.arquisoft.usuarios.domain.usuario.model;

import com.arquisoft.usuarios.domain.usuario.exception.RolUsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioRoleTest {

    @Test
    void debeRetornarCodigoCorrecto_cuandoRolEstudiante() {
        assertThat(UsuarioRole.ESTUDIANTE.getCodigo()).isEqualTo("estudiante");
    }

    @Test
    void debeRetornarCodigoCorrecto_cuandoRolAsesorFicha() {
        assertThat(UsuarioRole.ASESOR_FICHA.getCodigo()).isEqualTo("asesor-ficha");
    }

    @Test
    void debeEncontrarRol_cuandoCodigoAsesorFichaExiste() {
        String codigo = "asesor-ficha";

        UsuarioRole rol = UsuarioRole.desdeCodigo(codigo);

        assertThat(rol).isEqualTo(UsuarioRole.ASESOR_FICHA);
    }

    @Test
    void debeLanzarExcepcion_cuandoCodigoInexistente() {
        String codigoInexistente = "admin";

        assertThatThrownBy(() -> UsuarioRole.desdeCodigo(codigoInexistente))
                .isInstanceOf(RolUsuarioNoEncontradoException.class)
                .hasMessageContaining("admin");
    }

    @Test
    void debeReportarValidez_sinLanzar_cuandoSeConsultaConEsCodigoValido() {
        assertThat(UsuarioRole.esCodigoValido("asesor-ficha")).isTrue();
        assertThat(UsuarioRole.esCodigoValido("admin")).isFalse();
        assertThat(UsuarioRole.esCodigoValido(null)).isFalse();
    }

    @Test
    void debeTener8Roles_cuandoSeListanTodos() {
        assertThat(UsuarioRole.values()).hasSize(8);
    }
}
