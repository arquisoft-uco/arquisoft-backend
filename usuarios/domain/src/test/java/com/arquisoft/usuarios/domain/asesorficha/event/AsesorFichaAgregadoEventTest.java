package com.arquisoft.usuarios.domain.asesorficha.event;

import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorFichaAgregadoEventTest {

    @Test
    void debeDeclararElTopic_enAsesorFichaAgregadoEvent() {
        // Assert
        assertThat(AsesorFichaAgregadoEvent.EVENT_TOPIC)
                .isEqualTo(EventTopics.Usuarios.ASESOR_FICHA_AGREGADO);
    }

    @Test
    void debeCargarLosCuatroCampos_enAsesorFichaAgregadoEvent() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act
        var evento = new AsesorFichaAgregadoEvent(usuario, "20161020123", "Ana Perez", "ana@uco.edu.co");

        // Assert
        assertThat(evento.getUsuario()).isEqualTo(usuario);
        assertThat(evento.getIdentificador()).isEqualTo("20161020123");
        assertThat(evento.getNombre()).isEqualTo("Ana Perez");
        assertThat(evento.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getIdEvento()).isNotNull();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
