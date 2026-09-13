package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.DestinatarioNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaDestinatario;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DestinatarioExisteRuleImplTest {

    private final DestinatarioExisteRuleImpl regla = new DestinatarioExisteRuleImpl();

    private static UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "ID-" + id, "Nombre " + id, id + "@uco.edu.co");
    }

    @Test
    void debeNoLanzar_cuandoElDestinatarioExiste() {
        // Act & Assert
        assertThatCode(() ->
                regla.validar(new ExistenciaDestinatario(UUID.randomUUID(), usuario(UUID.randomUUID()))))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarDestinatarioNoEncontrado_cuandoElDestinatarioNoExiste() {
        // Arrange
        UUID usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new ExistenciaDestinatario(usuario, UsuarioDomain.VACIO)))
                .isInstanceOf(DestinatarioNoEncontradoException.class)
                .hasMessageContaining(usuario.toString());
    }
}
