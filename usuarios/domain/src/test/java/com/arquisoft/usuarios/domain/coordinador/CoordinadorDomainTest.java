package com.arquisoft.usuarios.domain.coordinador;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordinadorDomainTest {

    @Test
    void debeCrearCoordinador_cuandoUsuarioEsValido() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act
        var coordinador = CoordinadorDomain.crear(usuario);

        // Assert
        assertThat(coordinador.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void debeLanzarValidacion_cuandoUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> CoordinadorDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var validationEx = (DomainValidationException) ex;
                    assertThat(validationEx.getValidationResult().getErrores())
                            .anySatisfy(error -> {
                                assertThat(error.campo()).isEqualTo(UsuariosFields.Coordinador.USUARIO);
                                assertThat(error.codigoError())
                                        .isEqualTo(UsuariosCodes.Coordinador.USUARIO_REQUERIDO);
                            });
                });
    }
}
