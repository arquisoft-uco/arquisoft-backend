package com.arquisoft.fichas.infrastructure.usuario.command.primaryadapter.amqp;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioCreadoPayloadTest {

    // El productor serializa el evento con el mismo mapper que el consumidor usa para leerlo, asi
    // que instanciar la configuracion real es lo unico que prueba el contrato de verdad: un doble
    // configurado a mano podria pasar el test y fallar en el broker.
    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    private static final class UsuarioCreadoEventoDePrueba extends DomainEvent {

        private final UUID usuarioId;
        private final String email;
        private final String rol;

        private UsuarioCreadoEventoDePrueba(UUID usuarioId, String email, String rol) {
            super(EventTopics.Usuarios.USUARIO_CREADO, "UsuarioCreadoEvent");
            this.usuarioId = usuarioId;
            this.email = email;
            this.rol = rol;
        }

        public UUID getUsuarioId() {
            return usuarioId;
        }

        public String getEmail() {
            return email;
        }

        public String getRol() {
            return rol;
        }
    }

    @Test
    void debeConservarIdEventoYOcurridoEn_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new UsuarioCreadoEventoDePrueba(
                UUID.randomUUID(), "juan.perez@soyuco.edu.co", "ESTUDIANTE");

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), UsuarioCreadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.usuarioId()).isEqualTo(evento.getUsuarioId().toString());
        assertThat(payload.email()).isEqualTo(evento.getEmail());
        assertThat(payload.rol()).isEqualTo(evento.getRol());
    }

    @Test
    void debeDejarOcurridoEnNulo_cuandoElProductorAunNoLoEnvia() {
        // Arrange — lectura tolerante: un productor viejo no rompe al consumidor
        String json = """
                {"idEvento":"evt-1","usuarioId":"11111111-1111-1111-1111-111111111111",
                 "email":"juan.perez@soyuco.edu.co","rol":"ESTUDIANTE"}
                """;

        // Act
        var payload = mapper.readValue(json, UsuarioCreadoPayload.class);

        // Assert
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
