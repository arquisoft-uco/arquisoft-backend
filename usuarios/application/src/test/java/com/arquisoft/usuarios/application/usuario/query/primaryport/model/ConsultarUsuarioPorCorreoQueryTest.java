package com.arquisoft.usuarios.application.usuario.query.primaryport.model;

import com.arquisoft.shared.exception.ApplicationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarUsuarioPorCorreoQueryTest {

    @Test
    void debeCrear_cuandoCorreoValido() {
        // Act
        var query = ConsultarUsuarioPorCorreoQuery.crear("  ana@uco.edu.co ");

        // Assert
        assertThat(query.correo()).isEqualTo("ana@uco.edu.co");
    }

    @Test
    void debeFallar400_cuandoCorreoVacioInvalidoOExcedeLongitud() {
        // Arrange
        var largo = "a".repeat(60) + "@uco.edu.co";

        // Act / Assert
        for (var correo : new String[]{null, "  ", "no-es-correo", largo}) {
            assertThatThrownBy(() -> ConsultarUsuarioPorCorreoQuery.crear(correo))
                    .isInstanceOf(ApplicationException.class);
        }
    }
}
