package com.arquisoft.usuarios.domain.usuario.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioModificadoEventTest {

    @Test
    void debeExponerLosDatosDelUsuario_cuandoSeConstruyeElEvento() {
        // Arrange
        var usuarioId = UUID.randomUUID();

        // Act
        var evento = new UsuarioModificadoEvent(usuarioId, "usr001", "Ana Perez", "ana@uco.edu.co");

        // Assert
        assertThat(evento.getUsuario()).isEqualTo(usuarioId);
        assertThat(evento.getIdentificador()).isEqualTo("usr001");
        assertThat(evento.getNombre()).isEqualTo("Ana Perez");
        assertThat(evento.getEmail()).isEqualTo("ana@uco.edu.co");
    }

    @Test
    void debeUsarElTopicDeTresSegmentos_cuandoSeConstruyeElEvento() {
        // Act — el constructor de DomainEvent rechaza un topic que no tenga 3 segmentos
        var evento = new UsuarioModificadoEvent(UUID.randomUUID(), "usr001", "Ana", "ana@uco.edu.co");

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("usuarios.usuario.modificado");
        assertThat(evento.getTipoEvento()).isEqualTo("UsuarioModificadoEvent");
    }

    @Test
    void debeAsignarIdYMomentoDeOcurrencia_cuandoSeConstruyeElEvento() {
        // Act
        var evento = new UsuarioModificadoEvent(UUID.randomUUID(), "usr001", "Ana", "ana@uco.edu.co");

        // Assert — el idEvento es la clave de idempotencia del consumidor
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
