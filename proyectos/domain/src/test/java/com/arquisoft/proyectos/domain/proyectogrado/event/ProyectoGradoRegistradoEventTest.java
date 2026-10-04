package com.arquisoft.proyectos.domain.proyectogrado.event;

import com.arquisoft.proyectos.domain.coordinador.model.ContactoCoordinador;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoRegistradoEventTest {

    @Test
    void debeExponerLosDatosDelCoordinador_cuandoSeConstruyeElEvento() {
        // Arrange
        var proyectoGrado = UUID.randomUUID();
        var fichaPerfil = UUID.randomUUID();
        var coordinador = new ContactoCoordinador("Laura Mesa", "laura.mesa@uco.edu.co");

        // Act
        var evento = new ProyectoGradoRegistradoEvent(proyectoGrado, fichaPerfil, "Sistema de gestión", coordinador);

        // Assert
        assertThat(evento.getProyectoGradoId()).isEqualTo(proyectoGrado);
        assertThat(evento.getFichaPerfilId()).isEqualTo(fichaPerfil);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getCoordinador()).isEqualTo(coordinador);
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Proyectos.PROYECTO_GRADO_REGISTRADO);
        assertThat(evento.getTipoEvento()).isEqualTo(ProyectoGradoRegistradoEvent.EVENT_TYPE);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
