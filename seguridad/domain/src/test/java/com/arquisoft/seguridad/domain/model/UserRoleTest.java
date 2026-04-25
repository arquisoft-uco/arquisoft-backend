package com.arquisoft.seguridad.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserRoleTest {

    @Test
    void debeRetornarCodigoCorrecto_cuandoRolEstudiante() {
        // Arrange / Act / Assert
        assertThat(UserRole.ESTUDIANTE.getCode()).isEqualTo("ESTUDIANTE");
    }

    @Test
    void debeRetornarSpringRole_cuandoRolAsesorFicha() {
        // Arrange / Act / Assert
        assertThat(UserRole.ASESOR_FICHA.getSpringRole()).isEqualTo("ROLE_ASESOR_FICHA");
    }

    @Test
    void debeEncontrarRol_cuandoCodigoAsesorFichaExiste() {
        // Arrange
        String codigo = "ASESOR_FICHA";

        // Act
        UserRole rol = UserRole.fromCode(codigo);

        // Assert
        assertThat(rol).isEqualTo(UserRole.ASESOR_FICHA);
    }

    @Test
    void debeLanzarExcepcion_cuandoCodigoInexistente() {
        // Arrange
        String codigoInexistente = "admin";

        // Act / Assert
        assertThatThrownBy(() -> UserRole.fromCode(codigoInexistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("admin");
    }

    @Test
    void debeTener8Roles_cuandoSeListanTodos() {
        // Arrange / Act / Assert
        assertThat(UserRole.values()).hasSize(8);
    }
}
