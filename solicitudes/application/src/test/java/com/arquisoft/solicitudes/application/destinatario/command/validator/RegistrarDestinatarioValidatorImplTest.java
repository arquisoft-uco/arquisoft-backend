package com.arquisoft.solicitudes.application.destinatario.command.validator;

import com.arquisoft.solicitudes.application.destinatario.command.validator.impl.RegistrarDestinatarioValidatorImpl;
import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;
import com.arquisoft.solicitudes.domain.destinatario.exception.DestinatarioNoEncontradoException;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarDestinatarioValidatorImplTest {

    private final RegistrarDestinatarioValidatorImpl validator = new RegistrarDestinatarioValidatorImpl();

    @Test
    void debeLanzarDestinatarioNoEncontrado_cuandoElUsuarioNoExisteEnElContexto() {
        // Arrange
        var destinatario = DestinatarioDomain.crear(UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(destinatario, UsuarioDomain.VACIO))
                .isInstanceOf(DestinatarioNoEncontradoException.class);
    }

    @Test
    void noDebeLanzar_cuandoElUsuarioExisteEnElContexto() {
        // Arrange
        var destinatario = DestinatarioDomain.crear(UUID.randomUUID());
        var usuario = UsuarioDomain.reconstruir(
                destinatario.getUsuario(), "ID-1", "Nombre", "nombre@uco.edu.co", Instant.now());

        // Act & Assert
        assertThatCode(() -> validator.validar(destinatario, usuario)).doesNotThrowAnyException();
    }
}
