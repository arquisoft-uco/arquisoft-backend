package com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MensajeNotificacionTest {

    @Test
    void debeCrearMensajeDeUnSoloDestinatario_cuandoSeUsaTextoPlano() {
        // Arrange
        DestinatarioNotificacion destinatario = new DestinatarioNotificacion("Ana", "ana@soyuco.edu.co");

        // Act
        MensajeNotificacion mensaje = MensajeNotificacion.textoPlano(destinatario, "Asunto", "Cuerpo");

        // Assert
        assertThat(mensaje.destinatarios()).containsExactly(destinatario);
        assertThat(mensaje.asunto()).isEqualTo("Asunto");
        assertThat(mensaje.cuerpo()).isEqualTo("Cuerpo");
        assertThat(mensaje.esHtml()).isFalse();
    }

    @Test
    void debeCopiarLaListaDeDestinatarios_cuandoSeConstruyeElMensaje() {
        // Arrange
        List<DestinatarioNotificacion> origen = new ArrayList<>();
        origen.add(new DestinatarioNotificacion("Ana", "ana@soyuco.edu.co"));

        // Act
        MensajeNotificacion mensaje = new MensajeNotificacion(origen, "Asunto", "Cuerpo", false);
        origen.add(new DestinatarioNotificacion("Luis", "luis@soyuco.edu.co"));

        // Assert
        assertThat(mensaje.destinatarios()).hasSize(1);
        assertThatThrownBy(() -> mensaje.destinatarios().add(
                new DestinatarioNotificacion("Otro", "otro@soyuco.edu.co")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void debeNormalizarANulaListaVacia_cuandoLosDestinatariosSonNull() {
        // Act
        MensajeNotificacion mensaje = new MensajeNotificacion(null, "Asunto", "Cuerpo", false);

        // Assert
        assertThat(mensaje.destinatarios()).isEmpty();
    }
}
