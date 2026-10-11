package com.arquisoft.usuarios.domain.administrador.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdministradorAgregadoEventTest {

    @Test
    void debeCargarTopicTipoYCampos_cuandoSeConstruyeElEvento() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new AdministradorAgregadoEvent(usuario, "20161020123", "Ana Perez", "ana@uco.edu.co");

        // Assert
        assertThat(AdministradorAgregadoEvent.EVENT_TOPIC)
                .isEqualTo(EventTopics.Usuarios.ADMINISTRADOR_AGREGADO);
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Usuarios.ADMINISTRADOR_AGREGADO);
        assertThat(evento.getTipoEvento()).isEqualTo(AdministradorAgregadoEvent.EVENT_TYPE);
        assertThat(evento.getUsuario()).isEqualTo(usuario);
        assertThat(evento.getIdentificador()).isEqualTo("20161020123");
        assertThat(evento.getNombre()).isEqualTo("Ana Perez");
        assertThat(evento.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getIdEvento()).isNotNull();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
