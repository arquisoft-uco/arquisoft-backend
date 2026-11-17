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

class ProyectoGradoEstadoCambiadoEventTest {

    @Test
    void debeUsarTopicConservarContenidoYCopiarListas_cuandoSeConstruye() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = new ContactoEstudiante(UtilUUID.generarNuevoUUID(), "Ana Ruiz", "ana.ruiz@soyuco.edu.co");
        var asesor = new ContactoAsesor("Luis Mora", "luis.mora@uco.edu.co");
        var estudiantes = new ArrayList<ContactoEstudiante>();
        estudiantes.add(estudiante);
        var asesores = new ArrayList<ContactoAsesor>();
        asesores.add(asesor);

        // Act
        var evento = new ProyectoGradoEstadoCambiadoEvent(
                proyectoGrado, "Sistema de gestión", "ATRASADO", "CANCELADO", "Falta de avance",
                estudiantes, asesores);
        estudiantes.clear();
        asesores.clear();

        // Assert
        assertThat(evento.getProyectoGradoId()).isEqualTo(proyectoGrado);
        assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(evento.getEstadoAnterior()).isEqualTo("ATRASADO");
        assertThat(evento.getEstadoNuevo()).isEqualTo("CANCELADO");
        assertThat(evento.getMotivo()).isEqualTo("Falta de avance");
        assertThat(evento.getEstudiantes()).containsExactly(estudiante);
        assertThat(evento.getAsesores()).containsExactly(asesor);
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Proyectos.PROYECTO_GRADO_ESTADO_CAMBIADO);
        assertThat(evento.getTipoEvento()).isEqualTo(ProyectoGradoEstadoCambiadoEvent.EVENT_TYPE);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
        assertThatThrownBy(() -> evento.getEstudiantes().add(estudiante))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> evento.getAsesores().add(asesor))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void debeAceptarListasVacias_cuandoNoHayDestinatarios() {
        // Act
        var evento = new ProyectoGradoEstadoCambiadoEvent(
                UtilUUID.generarNuevoUUID(), "P", "EN_PROCESO", "FINALIZADO", "", List.of(), List.of());

        // Assert
        assertThat(evento.getEstudiantes()).isEmpty();
        assertThat(evento.getAsesores()).isEmpty();
    }
}
