package com.arquisoft.proyectos.domain.proyectogrado.event;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoFinalizadoEventTest {

    @Test
    void debeExponerLosDatosTopicYTipo_cuandoSeConstruyeElEvento() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = new ContactoEstudiante(UtilUUID.generarNuevoUUID(), "Ana Ruiz", "ana.ruiz@soyuco.edu.co");
        var estudiantes = new ArrayList<ContactoEstudiante>();
        estudiantes.add(estudiante);

        // Act
        var evento = new ProyectoGradoFinalizadoEvent(proyectoGrado, "Sistema de gestión", estudiantes);
        estudiantes.clear();

        // Assert
        assertThat(evento.getProyectoGradoId()).isEqualTo(proyectoGrado);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getEstudiantes()).containsExactly(estudiante);
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Proyectos.PROYECTO_GRADO_FINALIZADO);
        assertThat(evento.getTipoEvento()).isEqualTo(ProyectoGradoFinalizadoEvent.EVENT_TYPE);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
        assertThatThrownBy(() -> evento.getEstudiantes().add(estudiante))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
