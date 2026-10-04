package com.arquisoft.usuarios.domain.usuario;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class EliminacionUsuarioDomainTest {

    @Test
    void debeCrearEliminacion_cuandoElUsuarioEsValido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var eliminacion = EliminacionUsuarioDomain.crear(usuario);

        // Assert
        assertThat(eliminacion.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void debeLanzarValidacion_cuandoElUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> EliminacionUsuarioDomain.crear(null))
                .isInstanceOfSatisfying(DomainValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("campo", "codigoError")
                                .containsExactly(tuple(UsuariosFields.Usuario.USUARIO,
                                        UsuariosCodes.Usuario.USUARIO_REQUERIDO)));
    }
}
