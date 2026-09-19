package com.arquisoft.usuarios.domain.estudiante;

import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteDomainTest {

    @Test
    void debeCrearEstudiante_cuandoUsuarioEsValido() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act
        var estudiante = EstudianteDomain.crear(usuario);

        // Assert
        assertThat(estudiante.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void debeLanzarValidacion_cuandoUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> EstudianteDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var validationEx = (DomainValidationException) ex;
                    assertThat(validationEx.getValidationResult().getErrores())
                            .anySatisfy(error -> {
                                assertThat(error.campo()).isEqualTo(UsuariosFields.Estudiante.USUARIO);
                                assertThat(error.codigoError())
                                        .isEqualTo(UsuariosCodes.Estudiante.USUARIO_REQUERIDO);
                            });
                });
    }

    @Test
    void debeReconstruirEstudiante_sinValidar() {
        // Act
        var estudiante = EstudianteDomain.reconstruir(null);

        // Assert
        assertThat(estudiante.getUsuario()).isNull();
    }
}
