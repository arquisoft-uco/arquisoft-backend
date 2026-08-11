package com.arquisoft.usuarios.infrastructure.usuario.command.adapter.out.persistence;

import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class UsuarioCommandOutputAdapterTest {

    private final UsuarioCommandOutputAdapter adapter = new UsuarioCommandOutputAdapter();

    @Test
    void debeNoLanzarExcepcion_cuandoSaveEsInvocado() {
        // Arrange
        UsuarioDomain usuario = UsuarioDomain.crear("test@example.com", UsuarioRole.ESTUDIANTE);

        // Act / Assert
        assertThatCode(() -> adapter.save(usuario)).doesNotThrowAnyException();
    }

    @Test
    void debeRetornarEmpty_cuandoFindByIdEsInvocado() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        Optional<UsuarioDomain> resultado = adapter.findById(id);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarFalse_cuandoExistePorEmailEsInvocado() {
        // Arrange / Act
        boolean existe = adapter.existePorEmail("test@example.com");

        // Assert
        assertThat(existe).isFalse();
    }
}
