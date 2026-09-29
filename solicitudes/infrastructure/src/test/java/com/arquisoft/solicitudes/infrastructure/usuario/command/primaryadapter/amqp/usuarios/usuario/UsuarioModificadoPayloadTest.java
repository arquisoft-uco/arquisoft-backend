package com.arquisoft.solicitudes.infrastructure.usuario.command.primaryadapter.amqp.usuarios.usuario;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioModificadoPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class UsuarioModificadoEventoDePrueba extends DomainEvent {

        private final UUID usuario;
        private final String identificador;
        private final String nombre;
        private final String email;

        private UsuarioModificadoEventoDePrueba(
                UUID usuario, String identificador, String nombre, String email) {
            super(EventTopics.Usuarios.USUARIO_MODIFICADO, "UsuarioModificadoEvent");
            this.usuario = usuario;
            this.identificador = identificador;
            this.nombre = nombre;
            this.email = email;
        }

        public UUID getUsuario() {
            return usuario;
        }

        public String getIdentificador() {
            return identificador;
        }

        public String getNombre() {
            return nombre;
        }

        public String getEmail() {
            return email;
        }
    }

    @Test
    void debeConservarIdEventoYOcurridoEn_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new UsuarioModificadoEventoDePrueba(
                UUID.randomUUID(), "EST-999", "Ana Actualizada", "actualizada@uco.edu.co");

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), UsuarioModificadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.usuario()).isEqualTo(evento.getUsuario().toString());
        assertThat(payload.identificador()).isEqualTo(evento.getIdentificador());
        assertThat(payload.nombre()).isEqualTo(evento.getNombre());
        assertThat(payload.email()).isEqualTo(evento.getEmail());
    }

    @Test
    void debeDeserializarConNull_cuandoFaltaUnCampoNuevo() {
        // Arrange
        String json = """
                {"idEvento":"evt-1","usuario":"11111111-1111-1111-1111-111111111111",
                 "identificador":"EST-999","nombre":"Ana Actualizada"}
                """;

        // Act
        var payload = mapper.readValue(json, UsuarioModificadoPayload.class);

        // Assert
        assertThat(payload.email()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
