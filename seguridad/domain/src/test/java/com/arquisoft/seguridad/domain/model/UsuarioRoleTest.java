package com.arquisoft.seguridad.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioRoleTest {

    @Test
    void debeRetornarCodigoCorrecto_cuandoRolEstudiante() {
        // Arrange / Act / Assert
        assertThat(UsuarioRole.ESTUDIANTE.getCode()).isEqualTo("ESTUDIANTE");
    }

    @Test
    void debeRetornarSpringRole_cuandoRolAsesorFicha() {
        // Arrange / Act / Assert
        assertThat(UsuarioRole.ASESOR_FICHA.getSpringRole()).isEqualTo("ROLE_ASESOR_FICHA");
    }

    @Test
    void debeEncontrarRol_cuandoCodigoAsesorFichaExiste() {
        // Arrange
        String codigo = "ASESOR_FICHA";

        // Act
        UsuarioRole rol = UsuarioRole.fromCode(codigo);

        // Assert
        assertThat(rol).isEqualTo(UsuarioRole.ASESOR_FICHA);
    }

    @Test
    void debeLanzarExcepcion_cuandoCodigoInexistente() {
        // Arrange
        String codigoInexistente = "admin";

        // Act / Assert
        assertThatThrownBy(() -> UsuarioRole.fromCode(codigoInexistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("admin");
    }

    @Test
    void debeTener8Roles_cuandoSeListanTodos() {
        // Arrange / Act / Assert
        assertThat(UsuarioRole.values()).hasSize(8);
    }
}
