package com.arquisoft.usuarios.domain.asesor;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorDomainTest {

    @Test
    void debeCrearAsesor_cuandoUsuarioEsValido() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act
        var asesor = AsesorDomain.crear(usuario);

        // Assert
        assertThat(asesor.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void debeLanzarValidacion_cuandoUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> AsesorDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var validationEx = (DomainValidationException) ex;
                    assertThat(validationEx.getValidationResult().getErrores())
                            .anySatisfy(error -> {
                                assertThat(error.campo()).isEqualTo(UsuariosFields.Asesor.USUARIO);
                                assertThat(error.codigoError())
                                        .isEqualTo(UsuariosCodes.Asesor.USUARIO_REQUERIDO);
                            });
                });
    }
}
