package com.arquisoft.usuarios.application.administrador.command.validator.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import com.arquisoft.usuarios.domain.administrador.exception.AdministradorUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarAdministradorValidatorImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final AgregarAdministradorValidatorImpl validator = new AgregarAdministradorValidatorImpl();

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "20161020123", NOMBRE, "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void noDebeLanzar_cuandoElUsuarioNoEsAdministradorOEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = AdministradorDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario(usuario), AdministradorDomain.VACIO))
                .doesNotThrowAnyException();
        assertThatCode(() -> validator.validar(usuario(usuario), eliminado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoYaEsAdministradorVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario(usuario), AdministradorDomain.crear(usuario)))
                .isInstanceOf(AdministradorUsuarioDuplicadoException.class)
                .extracting(ex -> ((AdministradorUsuarioDuplicadoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Administrador.USUARIO_DUPLICADO);
    }
}
