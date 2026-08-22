package com.arquisoft.usuarios.application.usuario.command.validator;

import com.arquisoft.usuarios.application.usuario.command.validator.impl.CrearUsuarioValidatorImpl;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioEmailDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CrearUsuarioValidatorTest {

    private final CrearUsuarioValidatorImpl crearUsuarioValidator = new CrearUsuarioValidatorImpl();

    @Test
    void debePasar_cuandoElEmailEstaLibre() {
        // Arrange
        UsuarioDomain usuario = UsuarioDomain.crear("ok@example.com", UsuarioRole.COORDINADOR);

        // Act / Assert
        assertThatCode(() -> crearUsuarioValidator.validar(usuario, false)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarEmailDuplicado_cuandoElEmailYaExiste() {
        // Arrange
        UsuarioDomain usuario = UsuarioDomain.crear("repetido@example.com", UsuarioRole.ASESOR);

        // Act / Assert
        assertThatThrownBy(() -> crearUsuarioValidator.validar(usuario, true))
                .isInstanceOf(UsuarioEmailDuplicadoException.class)
                .hasMessageContaining("repetido@example.com");
    }

    @Test
    void debeUsarElEmailNormalizadoPorElAgregado_cuandoLlegaConEspaciosYMayusculas() {
        // Arrange
        UsuarioDomain usuario = UsuarioDomain.crear("  Nuevo@Example.COM  ", UsuarioRole.ESTUDIANTE);

        // Act / Assert — el mensaje prueba que a la regla llego el email ya normalizado
        assertThatThrownBy(() -> crearUsuarioValidator.validar(usuario, true))
                .isInstanceOf(UsuarioEmailDuplicadoException.class)
                .hasMessageContaining("nuevo@example.com");
    }
}
