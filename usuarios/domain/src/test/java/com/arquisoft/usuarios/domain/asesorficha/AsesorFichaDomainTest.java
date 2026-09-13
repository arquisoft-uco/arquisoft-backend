package com.arquisoft.usuarios.domain.asesorficha;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorFichaDomainTest {

    @Test
    void debeCrearAsesorFicha_cuandoUsuarioEsValido() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act
        var asesorFicha = AsesorFichaDomain.crear(usuario);

        // Assert
        assertThat(asesorFicha.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void debeLanzarValidacion_cuandoUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> AsesorFichaDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var validationEx = (DomainValidationException) ex;
                    assertThat(validationEx.getValidationResult().getErrores())
                            .anySatisfy(error -> {
                                assertThat(error.campo()).isEqualTo(UsuariosFields.AsesorFicha.USUARIO);
                                assertThat(error.codigoError())
                                        .isEqualTo(UsuariosCodes.AsesorFicha.USUARIO_REQUERIDO);
                            });
                });
    }

    @Test
    void debeReconstruirAsesorFicha_sinValidar() {
        // Act
        var asesorFicha = AsesorFichaDomain.reconstruir(null);

        // Assert
        assertThat(asesorFicha.getUsuario()).isNull();
    }
}
