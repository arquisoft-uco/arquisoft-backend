package com.arquisoft.proyectos.domain.asesorproyectogrado.event;

import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorProyectoGradoAsignadoEventTest {

    @Test
    void debeExponerLosDatosTopicYTipo_cuandoSeConstruyeElEvento() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = new ContactoAsesor("Luis Diaz", "luis.diaz@soyuco.edu.co");

        // Act
        var evento = new AsesorProyectoGradoAsignadoEvent(proyectoGrado, "Sistema de gestión", asesor);

        // Assert
        assertThat(evento.getProyectoGradoId()).isEqualTo(proyectoGrado);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getAsesor()).isEqualTo(asesor);
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Proyectos.ASESOR_PROYECTO_GRADO_ASIGNADO);
        assertThat(evento.getTipoEvento()).isEqualTo(AsesorProyectoGradoAsignadoEvent.EVENT_TYPE);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
