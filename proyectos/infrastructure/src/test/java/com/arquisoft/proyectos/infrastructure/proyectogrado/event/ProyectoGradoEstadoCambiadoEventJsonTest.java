package com.arquisoft.proyectos.infrastructure.proyectogrado.event;

import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.proyectos.domain.coordinador.model.ContactoCoordinador;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.proyectos.domain.proyectogrado.event.ProyectoGradoEstadoCambiadoEvent;
import com.arquisoft.proyectos.domain.proyectogrado.event.ProyectoGradoRegistradoEvent;
import com.arquisoft.proyectos.domain.proyectogrado.model.DestinatariosEstadoProyectoGrado;
import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoEstadoCambiadoEventJsonTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    @Test
    void debeSerializarCoordinadorPlano_cuandoSePublicaElEstadoCambiado() {
        // Arrange
        var coordinador = new ContactoCoordinador(UtilUUID.generarNuevoUUID(), "Laura Mesa", "laura.mesa@uco.edu.co");
        var estudiante = new ContactoEstudiante(UtilUUID.generarNuevoUUID(), "Ana Ruiz", "ana.ruiz@soyuco.edu.co");
        var asesor = new ContactoAsesor("Mario Peña", "mario.pena@uco.edu.co");
        var evento = new ProyectoGradoEstadoCambiadoEvent(UtilUUID.generarNuevoUUID(), "Sistema", "EN_PROCESO",
                "ATRASADO", "", new DestinatariosEstadoProyectoGrado(coordinador, List.of(estudiante), List.of(asesor)));

        // Act
        var json = mapper.readTree(mapper.writeValueAsString(evento));

        // Assert
        assertThat(json.get("coordinador").get("id").asString()).isEqualTo(coordinador.id().toString());
        assertThat(json.get("coordinador").get("nombre").asString()).isEqualTo("Laura Mesa");
        assertThat(json.get("coordinador").get("email").asString()).isEqualTo("laura.mesa@uco.edu.co");
        assertThat(json.get("estudiantes")).hasSize(1);
        assertThat(json.get("asesores")).hasSize(1);
        assertThat(json.has("destinatarios")).isFalse();
        assertThat(json.get("estadoNuevo").asString()).isEqualTo("ATRASADO");
    }

    @Test
    void debeSerializarElIdDelCoordinador_cuandoSePublicaElRegistro() {
        // Arrange
        var coordinador = new ContactoCoordinador(UtilUUID.generarNuevoUUID(), "Laura Mesa", "laura.mesa@uco.edu.co");
        var evento = new ProyectoGradoRegistradoEvent(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Sistema", coordinador);

        // Act
        var json = mapper.readTree(mapper.writeValueAsString(evento));

        // Assert
        assertThat(json.get("coordinador").get("id").asString()).isEqualTo(coordinador.id().toString());
    }
}
