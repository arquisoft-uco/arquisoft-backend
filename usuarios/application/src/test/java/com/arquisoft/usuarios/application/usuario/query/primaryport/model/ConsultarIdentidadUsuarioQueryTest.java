package com.arquisoft.usuarios.application.usuario.query.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarIdentidadUsuarioQueryTest {

    @Test
    void debeCrearQuery_cuandoElUsuarioEsValido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarIdentidadUsuarioQuery.crear(usuario);

        // Assert
        assertThat(query.usuario()).isEqualTo(usuario);
    }

    @Test
    void debeAcumularErrorDeEntrada_cuandoElUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarIdentidadUsuarioQuery.crear(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(UsuariosFields.Usuario.USUARIO,
                                        UsuariosCodes.Usuario.USUARIO_REQUERIDO)));
    }
}
