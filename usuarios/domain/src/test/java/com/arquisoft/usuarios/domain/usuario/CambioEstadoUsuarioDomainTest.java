package com.arquisoft.usuarios.domain.usuario;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class CambioEstadoUsuarioDomainTest {

    @Test
    void debeCrearCambio_cuandoUsuarioYEstadoSonValidos() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var cambio = CambioEstadoUsuarioDomain.crear(usuario, EstadoUsuario.INACTIVO.getId());

        // Assert
        assertThat(cambio.getUsuario()).isEqualTo(usuario);
        assertThat(cambio.getEstado()).isEqualTo(EstadoUsuario.INACTIVO);
    }

    @Test
    void debeResolverElEstadoRecortado_cuandoLlegaConEspacios() {
        // Act
        var cambio = CambioEstadoUsuarioDomain.crear(UtilUUID.generarNuevoUUID(), "  ACTIVO ");

        // Assert
        assertThat(cambio.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
    }

    @Test
    void debeRechazarConEstadoInvalido_cuandoElEstadoNoPerteneceAlCatalogo() {
        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoUsuarioDomain.crear(UtilUUID.generarNuevoUUID(), "BLOQUEADO"))
                .isInstanceOfSatisfying(DomainValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("campo", "codigoError")
                                .containsExactly(
                                        tuple(UsuariosFields.Usuario.ESTADO, UsuariosCodes.Usuario.ESTADO_INVALIDO)));
    }

    @Test
    void debeAcumularAmbosErrores_cuandoUsuarioYEstadoSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoUsuarioDomain.crear(null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("campo", "codigoError")
                                .containsExactlyInAnyOrder(
                                        tuple(UsuariosFields.Usuario.USUARIO, UsuariosCodes.Usuario.USUARIO_REQUERIDO),
                                        tuple(UsuariosFields.Usuario.ESTADO, UsuariosCodes.Usuario.ESTADO_REQUERIDO)));
    }
}
