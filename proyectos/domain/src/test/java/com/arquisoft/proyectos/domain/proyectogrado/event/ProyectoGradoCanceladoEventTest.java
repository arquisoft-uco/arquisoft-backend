package com.arquisoft.proyectos.domain.proyectogrado.event;

import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoCanceladoEventTest {

    @Test
    void debeUsarTopicYConservarContenido_cuandoSeConstruye() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = new ContactoEstudiante(UtilUUID.generarNuevoUUID(), "Ana Ruiz", "ana.ruiz@soyuco.edu.co");
        var asesor = new ContactoAsesor("Luis Mora", "luis.mora@uco.edu.co");
        var estudiantes = new ArrayList<ContactoEstudiante>();
        estudiantes.add(estudiante);

        // Act
        var evento = new ProyectoGradoCanceladoEvent(
                proyectoGrado, "Sistema de gestión", "Falta de avance", estudiantes, List.of(asesor));
        estudiantes.clear();

        // Assert
        assertThat(evento.getProyectoGradoId()).isEqualTo(proyectoGrado);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getMotivo()).isEqualTo("Falta de avance");
        assertThat(evento.getEstudiantes()).containsExactly(estudiante);
        assertThat(evento.getAsesores()).containsExactly(asesor);
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Proyectos.PROYECTO_GRADO_CANCELADO);
        assertThat(evento.getTipoEvento()).isEqualTo(ProyectoGradoCanceladoEvent.EVENT_TYPE);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
        assertThatThrownBy(() -> evento.getEstudiantes().add(estudiante))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
