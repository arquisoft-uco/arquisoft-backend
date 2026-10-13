package com.arquisoft.proyectos.domain.estudianteproyectogrado.event;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudiantesProyectoGradoAsignadosEventTest {

    @Test
    void debeExponerLosDatosYUnaCopiaDeLosEstudiantes_cuandoSeConstruyeElEvento() {
        // Arrange
        var proyectoGrado = UUID.randomUUID();
        var estudiante = new ContactoEstudiante("Ana Gomez", "ana.gomez@soyuco.edu.co");
        var estudiantes = new ArrayList<>(List.of(estudiante));

        // Act
        var evento = new EstudiantesProyectoGradoAsignadosEvent(proyectoGrado, "Sistema de gestión", estudiantes);
        estudiantes.clear();

        // Assert
        assertThat(evento.getProyectoGradoId()).isEqualTo(proyectoGrado);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getEstudiantes()).containsExactly(estudiante);
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Proyectos.ESTUDIANTES_PROYECTO_GRADO_ASIGNADOS);
        assertThat(evento.getTipoEvento()).isEqualTo(EstudiantesProyectoGradoAsignadosEvent.EVENT_TYPE);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
