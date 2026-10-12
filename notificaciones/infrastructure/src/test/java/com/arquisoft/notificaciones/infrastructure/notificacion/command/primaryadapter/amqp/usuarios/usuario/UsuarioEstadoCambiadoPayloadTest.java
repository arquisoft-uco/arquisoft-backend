package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.usuarios.usuario;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioEstadoCambiadoPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class UsuarioEstadoCambiadoEventoDePrueba extends DomainEvent {

        private final UUID usuario;
        private final String nombre;
        private final String email;
        private final String estado;
        private final String estadoNombre;

        private UsuarioEstadoCambiadoEventoDePrueba(UUID usuario, String nombre, String email, String estado,
                                                    String estadoNombre) {
            super(EventTopics.Usuarios.USUARIO_ESTADO_CAMBIADO, "UsuarioEstadoCambiadoEvent");
            this.usuario = usuario;
            this.nombre = nombre;
            this.email = email;
            this.estado = estado;
            this.estadoNombre = estadoNombre;
        }

        public UUID getUsuario() {
            return usuario;
        }

        public String getNombre() {
            return nombre;
        }

        public String getEmail() {
            return email;
        }

        public String getEstado() {
            return estado;
        }

        public String getEstadoNombre() {
            return estadoNombre;
        }
    }

    @Test
    void debeConservarTodosLosCampos_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new UsuarioEstadoCambiadoEventoDePrueba(UtilUUID.generarNuevoUUID(), "Ana Perez",
                "ana.perez@uco.edu.co", "ACTIVO", "Activo");

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), UsuarioEstadoCambiadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.usuario()).isEqualTo(evento.getUsuario().toString());
        assertThat(payload.nombre()).isEqualTo(evento.getNombre());
        assertThat(payload.email()).isEqualTo(evento.getEmail());
        assertThat(payload.estado()).isEqualTo(evento.getEstado());
        assertThat(payload.estadoNombre()).isEqualTo(evento.getEstadoNombre());
    }

    @Test
    void debeDejarNuloElCampoAusente_cuandoElProductorNoLoEnvia() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","usuario":"11111111-1111-1111-1111-111111111111",
                 "nombre":"Ana Perez","email":"ana.perez@uco.edu.co","estado":"ACTIVO"}
                """;

        // Act
        var payload = mapper.readValue(json, UsuarioEstadoCambiadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.estadoNombre()).isNull();
    }
}
