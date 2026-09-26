package com.arquisoft.usuarios.domain.asesor.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorRemovidoEventTest {

    @Test
    void debeDeclararTopicYCargarLosCuatroCampos_enAsesorRemovidoEvent() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new AsesorRemovidoEvent(usuario, "1036950123", "Carlos Rios", "carlos@uco.edu.co");

        // Assert
        assertThat(AsesorRemovidoEvent.EVENT_TOPIC).isEqualTo(EventTopics.Usuarios.ASESOR_REMOVIDO);
        assertThat(evento.getUsuario()).isEqualTo(usuario);
        assertThat(evento.getIdentificador()).isEqualTo("1036950123");
        assertThat(evento.getNombre()).isEqualTo("Carlos Rios");
        assertThat(evento.getEmail()).isEqualTo("carlos@uco.edu.co");
        assertThat(evento.getIdEvento()).isNotNull();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
