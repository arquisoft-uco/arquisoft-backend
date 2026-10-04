package com.arquisoft.usuarios.application.administrador.command.validator.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import com.arquisoft.usuarios.domain.administrador.exception.AdministradorUsuarioDuplicadoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarAdministradorValidatorImplTest {

    private final AgregarAdministradorValidatorImpl validator = new AgregarAdministradorValidatorImpl();

    @Test
    void noDebeLanzar_cuandoElUsuarioNoEsAdministradorOEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = AdministradorDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario, AdministradorDomain.VACIO))
                .doesNotThrowAnyException();
        assertThatCode(() -> validator.validar(usuario, eliminado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoYaEsAdministradorVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, AdministradorDomain.crear(usuario)))
                .isInstanceOf(AdministradorUsuarioDuplicadoException.class)
                .extracting(ex -> ((AdministradorUsuarioDuplicadoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Administrador.USUARIO_DUPLICADO);
    }
}
