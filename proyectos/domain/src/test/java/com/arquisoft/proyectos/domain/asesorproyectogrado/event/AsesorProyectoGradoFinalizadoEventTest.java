package com.arquisoft.proyectos.domain.asesorproyectogrado.event;

import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorProyectoGradoFinalizadoEventTest {

    @Test
    void debeExponerLosDatosTopicYTipo_cuandoSeConstruyeElEvento() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesorId = UtilUUID.generarNuevoUUID();
        var asesor = new ContactoAsesor("Luis Diaz", "luis.diaz@soyuco.edu.co");

        // Act
        var evento = new AsesorProyectoGradoFinalizadoEvent(proyectoGrado, "Sistema de gestión", asesorId, asesor);

        // Assert
        assertThat(evento.getProyectoGradoId()).isEqualTo(proyectoGrado);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getAsesorId()).isEqualTo(asesorId);
        assertThat(evento.getAsesor()).isEqualTo(asesor);
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Proyectos.ASESOR_PROYECTO_GRADO_FINALIZADO);
        assertThat(evento.getTipoEvento()).isEqualTo(AsesorProyectoGradoFinalizadoEvent.EVENT_TYPE);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
