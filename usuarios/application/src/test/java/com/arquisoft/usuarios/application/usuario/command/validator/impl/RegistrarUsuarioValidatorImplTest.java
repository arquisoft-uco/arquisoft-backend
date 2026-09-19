package com.arquisoft.usuarios.application.usuario.command.validator.impl;

import com.arquisoft.usuarios.domain.usuario.RegistroUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioContactoDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioEmailDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioIdentificadorDuplicadoException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarUsuarioValidatorImplTest {

    private final RegistrarUsuarioValidatorImpl validator = new RegistrarUsuarioValidatorImpl();

    private RegistroUsuarioDomain registro() {
        return RegistroUsuarioDomain.crear(
                "usr001", "Ana Pérez", "ana@uco.edu.co", "573001112233", "Ana", "Pérez", List.of());
    }

    @Test
    void noDebeLanzarExcepcion_cuandoNoHayDuplicados() {
        // Act & Assert
        assertThatCode(() -> validator.validar(registro(), false, false, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarIdentificadorDuplicado_cuandoIdentificadorYaExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro(), true, false, false))
                .isInstanceOf(UsuarioIdentificadorDuplicadoException.class);
    }

    @Test
    void debeLanzarEmailDuplicado_cuandoEmailYaExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro(), false, true, false))
                .isInstanceOf(UsuarioEmailDuplicadoException.class);
    }

    @Test
    void debeLanzarContactoDuplicado_cuandoContactoYaExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro(), false, false, true))
                .isInstanceOf(UsuarioContactoDuplicadoException.class);
    }

    @Test
    void debePriorizarIdentificadorDuplicado_cuandoVariosDuplicadosALaVez() {
        // Act & Assert — RN-07 corre antes que RN-08/RN-09
        assertThatThrownBy(() -> validator.validar(registro(), true, true, true))
                .isInstanceOf(UsuarioIdentificadorDuplicadoException.class);
    }
}
