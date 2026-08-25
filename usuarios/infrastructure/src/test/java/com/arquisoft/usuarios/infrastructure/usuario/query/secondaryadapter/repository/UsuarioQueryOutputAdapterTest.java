package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioQueryOutputAdapterTest {

    private final UsuarioQueryOutputAdapter adapter = new UsuarioQueryOutputAdapter();

    @Test
    void debeRetornarSiempreTrue_cuandoExistsById() {
        // Arrange — mock temporal: siempre retorna true
        UUID cualquierId = UUID.randomUUID();

        // Act
        Boolean resultado = adapter.existsById(cualquierId);

        // Assert
        assertThat(resultado).isTrue();
    }
}
