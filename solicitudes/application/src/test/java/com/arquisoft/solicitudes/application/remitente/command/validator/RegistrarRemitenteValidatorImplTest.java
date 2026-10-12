package com.arquisoft.solicitudes.application.remitente.command.validator;

import com.arquisoft.solicitudes.application.remitente.command.validator.impl.RegistrarRemitenteValidatorImpl;
import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;
import com.arquisoft.solicitudes.domain.remitente.exception.RemitenteNoEncontradoException;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarRemitenteValidatorImplTest {

    private final RegistrarRemitenteValidatorImpl validator = new RegistrarRemitenteValidatorImpl();

    @Test
    void debeLanzarRemitenteNoEncontrado_cuandoElUsuarioNoExisteEnElContexto() {
        // Arrange
        var remitente = RemitenteDomain.crear(UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(remitente, UsuarioDomain.VACIO))
                .isInstanceOf(RemitenteNoEncontradoException.class);
    }

    @Test
    void noDebeLanzar_cuandoElUsuarioExisteEnElContexto() {
        // Arrange
        var remitente = RemitenteDomain.crear(UUID.randomUUID());
        var usuario = UsuarioDomain.reconstruir(
                remitente.getUsuario(), "ID-1", "Nombre", "nombre@uco.edu.co", Instant.now());

        // Act & Assert
        assertThatCode(() -> validator.validar(remitente, usuario)).doesNotThrowAnyException();
    }
}
