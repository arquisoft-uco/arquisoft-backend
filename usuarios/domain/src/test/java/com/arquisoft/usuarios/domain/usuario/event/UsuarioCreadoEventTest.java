package com.arquisoft.usuarios.domain.usuario.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioCreadoEventTest {

    @Test
    void debeAsignarMetadatosYPayload_cuandoEventoEsCreado() {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        String email = "test@example.com";
        String rol = "estudiante";

        // Act
        UsuarioCreadoEvent evento = new UsuarioCreadoEvent(usuarioId, email, rol);

        // Assert
        assertThat(evento.getIdEvento()).isNotNull();
        assertThat(evento.getOcurridoEn()).isNotNull();
        assertThat(evento.getTipoEvento()).isEqualTo("UsuarioCreadoEvent");
        assertThat(evento.getTemaEvento()).isEqualTo("usuarios.usuario.creado");
        assertThat(evento.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(evento.getEmail()).isEqualTo(email);
        assertThat(evento.getRol()).isEqualTo(rol);
    }
}
