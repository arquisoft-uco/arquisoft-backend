package com.arquisoft.proyectos.domain.estudianteproyectogrado.event;

import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteProyectoGradoDestituidoEventTest {

    @Test
    void debeExponerProyectoYEstudiante_cuandoSeConstruyeElEvento() {
        // Arrange
        var proyectoGrado = UUID.randomUUID();
        var estudiante = UUID.randomUUID();

        // Act
        var evento = new EstudianteProyectoGradoDestituidoEvent(proyectoGrado, estudiante);

        // Assert
        assertThat(evento.getProyectoGradoId()).isEqualTo(proyectoGrado);
        assertThat(evento.getEstudianteId()).isEqualTo(estudiante);
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Proyectos.ESTUDIANTE_PROYECTO_GRADO_DESTITUIDO);
        assertThat(evento.getTipoEvento()).isEqualTo(EstudianteProyectoGradoDestituidoEvent.EVENT_TYPE);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
