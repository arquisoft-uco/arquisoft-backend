package com.arquisoft.usuarios.domain.bibliotecario.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BibliotecarioRemovidoEventTest {

    @Test
    void debeDeclararTopicYCargarLosCuatroCampos_enBibliotecarioRemovidoEvent() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new BibliotecarioRemovidoEvent(usuario, "20161020123", "Ana Perez", "ana@uco.edu.co");

        // Assert
        assertThat(BibliotecarioRemovidoEvent.EVENT_TOPIC).isEqualTo(EventTopics.Usuarios.BIBLIOTECARIO_REMOVIDO);
        assertThat(evento.getUsuario()).isEqualTo(usuario);
        assertThat(evento.getIdentificador()).isEqualTo("20161020123");
        assertThat(evento.getNombre()).isEqualTo("Ana Perez");
        assertThat(evento.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getIdEvento()).isNotNull();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
