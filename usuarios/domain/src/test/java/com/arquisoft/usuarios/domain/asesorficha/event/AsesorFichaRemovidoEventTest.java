package com.arquisoft.usuarios.domain.asesorficha.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorFichaRemovidoEventTest {

    @Test
    void debeDeclararTopicYCargarLosCuatroCampos_enAsesorFichaRemovidoEvent() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new AsesorFichaRemovidoEvent(usuario, "1036950123", "Laura Gomez", "laura@uco.edu.co");

        // Assert
        assertThat(AsesorFichaRemovidoEvent.EVENT_TOPIC).isEqualTo(EventTopics.Usuarios.ASESOR_FICHA_REMOVIDO);
        assertThat(evento.getUsuario()).isEqualTo(usuario);
        assertThat(evento.getIdentificador()).isEqualTo("1036950123");
        assertThat(evento.getNombre()).isEqualTo("Laura Gomez");
        assertThat(evento.getEmail()).isEqualTo("laura@uco.edu.co");
        assertThat(evento.getIdEvento()).isNotNull();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
