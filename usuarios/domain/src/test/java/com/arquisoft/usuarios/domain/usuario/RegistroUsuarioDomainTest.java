package com.arquisoft.usuarios.domain.usuario;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistroUsuarioDomainTest {

    @Test
    void debeCrearRegistro_cuandoDatosValidos() {
        // Arrange & Act
        var registro = RegistroUsuarioDomain.crear(
                "usr001", "Ana María Pérez Gómez", "ANA@UCO.EDU.CO", "573445670023",
                "Ana María", "Pérez Gómez", List.of("estudiante"));

        // Assert
        assertThat(registro.getIdentificador()).isEqualTo("usr001");
        assertThat(registro.getNombre()).isEqualTo("Ana María Pérez Gómez");
        assertThat(registro.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(registro.getContacto()).isEqualTo("573445670023");
        assertThat(registro.getNombres()).isEqualTo("Ana María");
        assertThat(registro.getApellidos()).isEqualTo("Pérez Gómez");
        assertThat(registro.getRoles()).containsExactly("estudiante");
    }

    @Test
    void debeAcumularTodosLosFieldErrors_cuandoVariosCamposInvalidos() {
        // Arrange & Act & Assert
        assertThatThrownBy(() -> RegistroUsuarioDomain.crear(
                "ab", "Juan3", "x", "", "Juan3", "Pérez", List.of()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    var codigos = errores.stream().map(e -> e.codigoError()).toList();
                    assertThat(codigos).contains(
                            UsuariosCodes.Usuario.IDENTIFICADOR_LONGITUD,
                            UsuariosCodes.Usuario.NOMBRE_FORMATO,
                            UsuariosCodes.Usuario.EMAIL_FORMATO,
                            UsuariosCodes.Usuario.CONTACTO_REQUERIDO);
                });
    }

    @Test
    void debeNormalizarEmailYRecortarNombre_cuandoTienenEspacios() {
        // Arrange & Act
        var registro = RegistroUsuarioDomain.crear(
                "  usr002  ", "  Juan Pérez  ", "  JUAN@UCO.EDU.CO  ", "573001112233",
                "Juan", "Pérez", null);

        // Assert
        assertThat(registro.getIdentificador()).isEqualTo("usr002");
        assertThat(registro.getNombre()).isEqualTo("Juan Pérez");
        assertThat(registro.getEmail()).isEqualTo("juan@uco.edu.co");
    }

    @Test
    void debeAceptarContactoDe12Digitos_cuandoIncluyePrefijoDePais() {
        // Arrange & Act
        var registro = RegistroUsuarioDomain.crear(
                "usr003", "Juan Pérez", "juan@uco.edu.co", "573445670023",
                "Juan", "Pérez", List.of());

        // Assert
        assertThat(registro.getContacto()).isEqualTo("573445670023");
    }

    @Test
    void debeRechazarContacto_cuandoLongitudInvalidaOFormatoInvalido() {
        // Arrange & Act & Assert — 9 dígitos (corto)
        assertThatThrownBy(() -> RegistroUsuarioDomain.crear(
                "usr004", "Juan Pérez", "juan@uco.edu.co", "123456789",
                "Juan", "Pérez", List.of()))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(UsuariosCodes.Usuario.CONTACTO_LONGITUD);

        // 16 dígitos (largo)
        assertThatThrownBy(() -> RegistroUsuarioDomain.crear(
                "usr005", "Juan Pérez", "juan@uco.edu.co", "1234567890123456",
                "Juan", "Pérez", List.of()))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(UsuariosCodes.Usuario.CONTACTO_LONGITUD);

        // con caracteres no numéricos
        assertThatThrownBy(() -> RegistroUsuarioDomain.crear(
                "usr006", "Juan Pérez", "juan@uco.edu.co", "+57 3445670023",
                "Juan", "Pérez", List.of()))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(UsuariosCodes.Usuario.CONTACTO_FORMATO);
    }

    @Test
    void debeLanzarExcepcion_cuandoNombresOApellidosEnBlanco() {
        // Arrange & Act & Assert
        assertThatThrownBy(() -> RegistroUsuarioDomain.crear(
                "usr007", "Juan Pérez", "juan@uco.edu.co", "573001112233",
                "  ", "Pérez", List.of()))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(UsuariosCodes.Usuario.NOMBRES_REQUERIDO);

        assertThatThrownBy(() -> RegistroUsuarioDomain.crear(
                "usr008", "Juan Pérez", "juan@uco.edu.co", "573001112233",
                "Juan", "", List.of()))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(UsuariosCodes.Usuario.APELLIDOS_REQUERIDO);
    }

    @Test
    void debeDevolverListaVaciaInmutable_cuandoRolesEsNull() {
        // Arrange & Act
        var registro = RegistroUsuarioDomain.crear(
                "usr009", "Juan Pérez", "juan@uco.edu.co", "573001112233",
                "Juan", "Pérez", null);

        // Assert
        assertThat(registro.getRoles()).isEmpty();
        assertThatThrownBy(() -> registro.getRoles().add("estudiante"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
