package com.arquisoft.usuarios.domain.administrador;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemocionAdministradorDomainTest {

    @Test
    void debeCrearRemocion_cuandoUsuarioYActorSonValidos() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var actor = UtilUUID.generarNuevoUUID();

        // Act
        var remocion = RemocionAdministradorDomain.crear(usuario, actor);

        // Assert
        assertThat(remocion.getUsuario()).isEqualTo(usuario);
        assertThat(remocion.getActor()).isEqualTo(actor);
    }

    @Test
    void debeAcumularAmbosErrores_cuandoUsuarioYActorSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> RemocionAdministradorDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(UsuariosFields.Administrador.USUARIO);
                        assertThat(error.codigoError()).isEqualTo(UsuariosCodes.Administrador.USUARIO_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(UsuariosFields.Administrador.ACTOR);
                        assertThat(error.codigoError()).isEqualTo(UsuariosCodes.Administrador.ACTOR_REQUERIDO);
                    });
                    assertThat(errores).hasSize(2);
                });
    }
}
