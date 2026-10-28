package com.arquisoft.usuarios.application.representantecomite.command.validator.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.representantecomite.exception.RepresentanteComiteUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarRepresentanteComiteValidatorImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final AgregarRepresentanteComiteValidatorImpl validator = new AgregarRepresentanteComiteValidatorImpl();

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "20161020123", NOMBRE, "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void noDebeLanzar_cuandoElUsuarioNoEsRepresentanteOEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = RepresentanteComiteDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario(usuario), RepresentanteComiteDomain.VACIO))
                .doesNotThrowAnyException();
        assertThatCode(() -> validator.validar(usuario(usuario), eliminado)).doesNotThrowAnyException();
    }

    @Test
    void debePropagarDuplicado_cuandoElUsuarioYaEsRepresentanteVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario(usuario), RepresentanteComiteDomain.crear(usuario)))
                .isInstanceOf(RepresentanteComiteUsuarioDuplicadoException.class)
                .extracting(ex -> ((RepresentanteComiteUsuarioDuplicadoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.RepresentanteComite.USUARIO_DUPLICADO);
    }
}
