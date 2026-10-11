package com.arquisoft.usuarios.domain.representantecomite.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RepresentanteComiteRemovidoEventTest {

    @Test
    void debeDeclararTopicYCargarLosCuatroCampos_enRepresentanteComiteRemovidoEvent() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new RepresentanteComiteRemovidoEvent(usuario, "20161020123", "Ana Perez", "ana@uco.edu.co");

        // Assert
        assertThat(RepresentanteComiteRemovidoEvent.EVENT_TOPIC)
                .isEqualTo(EventTopics.Usuarios.REPRESENTANTE_COMITE_REMOVIDO);
        assertThat(evento.getTipoEvento()).isEqualTo(RepresentanteComiteRemovidoEvent.EVENT_TYPE);
        assertThat(evento.getUsuario()).isEqualTo(usuario);
        assertThat(evento.getIdentificador()).isEqualTo("20161020123");
        assertThat(evento.getNombre()).isEqualTo("Ana Perez");
        assertThat(evento.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getIdEvento()).isNotNull();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
