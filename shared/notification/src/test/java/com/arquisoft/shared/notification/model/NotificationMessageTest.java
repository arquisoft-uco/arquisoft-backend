package com.arquisoft.shared.notification.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificationMessageTest {

    @Test
    void debeCrearMensajeDeUnSoloDestinatario_cuandoSeUsaTextoPlano() {
        // Arrange
        NotificationRecipient destinatario = new NotificationRecipient("Ana", "ana@soyuco.edu.co");

        // Act
        NotificationMessage mensaje = NotificationMessage.textoPlano(destinatario, "Asunto", "Cuerpo");

        // Assert
        assertThat(mensaje.destinatarios()).containsExactly(destinatario);
        assertThat(mensaje.asunto()).isEqualTo("Asunto");
        assertThat(mensaje.cuerpo()).isEqualTo("Cuerpo");
        assertThat(mensaje.esHtml()).isFalse();
    }

    @Test
    void debeCopiarLaListaDeDestinatarios_cuandoSeConstruyeElMensaje() {
        // Arrange — una lista mutable que el llamador podria modificar despues
        List<NotificationRecipient> origen = new ArrayList<>();
        origen.add(new NotificationRecipient("Ana", "ana@soyuco.edu.co"));

        // Act
        NotificationMessage mensaje = new NotificationMessage(origen, "Asunto", "Cuerpo", false);
        origen.add(new NotificationRecipient("Luis", "luis@soyuco.edu.co"));

        // Assert — el mensaje conserva la foto del momento de construccion
        assertThat(mensaje.destinatarios()).hasSize(1);
        assertThatThrownBy(() -> mensaje.destinatarios().add(
                new NotificationRecipient("Otro", "otro@soyuco.edu.co")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void debeNormalizarANulaListaVacia_cuandoLosDestinatariosSonNull() {
        // Act
        NotificationMessage mensaje = new NotificationMessage(null, "Asunto", "Cuerpo", false);

        // Assert
        assertThat(mensaje.destinatarios()).isEmpty();
    }
}
