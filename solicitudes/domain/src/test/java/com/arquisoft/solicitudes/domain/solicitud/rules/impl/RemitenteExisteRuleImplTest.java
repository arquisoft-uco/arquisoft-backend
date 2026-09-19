package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.RemitenteNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaRemitente;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemitenteExisteRuleImplTest {

    private final RemitenteExisteRuleImpl regla = new RemitenteExisteRuleImpl();

    private static UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "ID-" + id, "Nombre " + id, id + "@uco.edu.co", Instant.now());
    }

    @Test
    void debeNoLanzar_cuandoElRemitenteExiste() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaRemitente(UUID.randomUUID(), usuario(UUID.randomUUID()))))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarRemitenteNoEncontrado_cuandoElRemitenteNoExiste() {
        // Arrange
        UUID usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new ExistenciaRemitente(usuario, UsuarioDomain.VACIO)))
                .isInstanceOf(RemitenteNoEncontradoException.class)
                .hasMessageContaining(usuario.toString());
    }
}
