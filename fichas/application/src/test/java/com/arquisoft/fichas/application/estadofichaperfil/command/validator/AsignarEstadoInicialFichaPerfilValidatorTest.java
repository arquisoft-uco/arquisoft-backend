package com.arquisoft.fichas.application.estadofichaperfil.command.validator;

import com.arquisoft.fichas.application.estadofichaperfil.command.validator.impl.AsignarEstadoInicialFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarEstadoInicialFichaPerfilValidatorTest {

    private final AsignarEstadoInicialFichaPerfilValidatorImpl validator =
            new AsignarEstadoInicialFichaPerfilValidatorImpl();

    @Test
    void debePasar_cuandoLaFichaExiste() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();

        // Act / Assert
        assertThatCode(() -> validator.validar(fichaPerfil, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarFichaNoEncontrada_cuandoLaFichaNoExiste() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();

        // Act / Assert — el identificador en el mensaje prueba que el validator armo el registro
        assertThatThrownBy(() -> validator.validar(fichaPerfil, false))
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(fichaPerfil.toString());
    }
}
