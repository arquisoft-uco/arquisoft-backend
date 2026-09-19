package com.arquisoft.usuarios.domain.asesor.event;

import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorAgregadoEventTest {

    @Test
    void debeDeclararElTopic_enAsesorAgregadoEvent() {
        // Assert
        assertThat(AsesorAgregadoEvent.EVENT_TOPIC)
                .isEqualTo(EventTopics.Usuarios.ASESOR_AGREGADO);
    }

    @Test
    void debeCargarLosCuatroCampos_enAsesorAgregadoEvent() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act
        var evento = new AsesorAgregadoEvent(usuario, "20161020123", "Ana Perez", "ana@uco.edu.co");

        // Assert
        assertThat(evento.getUsuario()).isEqualTo(usuario);
        assertThat(evento.getIdentificador()).isEqualTo("20161020123");
        assertThat(evento.getNombre()).isEqualTo("Ana Perez");
        assertThat(evento.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getIdEvento()).isNotNull();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
