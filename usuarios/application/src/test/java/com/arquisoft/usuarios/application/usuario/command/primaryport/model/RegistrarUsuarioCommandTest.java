package com.arquisoft.usuarios.application.usuario.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarUsuarioCommandTest {

    @Test
    void debeCrearCommand_cuandoRolesVacioOAusente() {
        // Arrange & Act
        var command = RegistrarUsuarioCommand.crear(
                "usr001", "Ana", "Pérez", "ana@uco.edu.co", "573001112233", null);

        // Assert
        assertThat(command.identificador()).isEqualTo("usr001");
        assertThat(command.nombres()).isEqualTo("Ana");
        assertThat(command.apellidos()).isEqualTo("Pérez");
        assertThat(command.email()).isEqualTo("ana@uco.edu.co");
        assertThat(command.contacto()).isEqualTo("573001112233");
        assertThat(command.roles()).isEmpty();
        assertThatThrownBy(() -> command.roles().add("x"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void debeConservarRoles_cuandoSonConocidos() {
        // Arrange & Act
        var command = RegistrarUsuarioCommand.crear(
                "usr002", "Juan", "Gómez", "juan@uco.edu.co", "573001112244",
                List.of("estudiante", "asesor"));

        // Assert
        assertThat(command.roles()).containsExactly("estudiante", "asesor");
    }

    @Test
    void debeLanzarExcepcion_cuandoRolDesconocido() {
        // Arrange & Act & Assert
        assertThatThrownBy(() -> RegistrarUsuarioCommand.crear(
                "usr003", "Juan", "Gómez", "juan@uco.edu.co", "573001112255", List.of("jefe")))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).anyMatch(e -> e.codigoError().equals(UsuariosCodes.Usuario.ROL_NO_VALIDO));
                });
    }

    @Test
    void debeLanzarExcepcion_cuandoRolEnBlanco() {
        // Arrange & Act & Assert
        assertThatThrownBy(() -> RegistrarUsuarioCommand.crear(
                "usr004", "Juan", "Gómez", "juan@uco.edu.co", "573001112266",
                Collections.singletonList("  ")))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).anyMatch(e -> e.codigoError().equals(UsuariosCodes.Usuario.ROL_REQUERIDO));
                });
    }

    @Test
    void debeAcumularErroresDeEntrada_cuandoCamposObligatoriosEnBlanco() {
        // Arrange & Act & Assert
        assertThatThrownBy(() -> RegistrarUsuarioCommand.crear("", "", "", "", "", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var codigos = ((ApplicationValidationException) ex).getValidationResult().getErrores()
                            .stream().map(e -> e.codigoError()).toList();
                    assertThat(codigos).contains(
                            UsuariosCodes.Usuario.IDENTIFICADOR_REQUERIDO,
                            UsuariosCodes.Usuario.NOMBRES_REQUERIDO,
                            UsuariosCodes.Usuario.APELLIDOS_REQUERIDO,
                            UsuariosCodes.Usuario.EMAIL_REQUERIDO,
                            UsuariosCodes.Usuario.CONTACTO_REQUERIDO);
                });
    }
}
