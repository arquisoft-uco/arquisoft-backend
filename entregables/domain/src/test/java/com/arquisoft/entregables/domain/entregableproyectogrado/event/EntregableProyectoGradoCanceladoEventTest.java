package com.arquisoft.entregables.domain.entregableproyectogrado.event;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntregableProyectoGradoCanceladoEventTest {

    @Test
    void debeExponerTopicCamposYEstadoFijo_cuandoSeConstruye() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudiantes = List.of(new ContactoEstudiante(UtilUUID.generarNuevoUUID(), "Ana", "ana@uco.edu.co"));

        // Act
        var evento = new EntregableProyectoGradoCanceladoEvent(entregable, proyecto, "Sistema de riego", 3, estudiantes);

        // Assert
        assertThat(evento.getTemaEvento())
                .isEqualTo(EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_CANCELADO)
                .isEqualTo("entregables.entregable_proyecto_grado.cancelado");
        assertThat(evento.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(evento.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(evento.getNombreProyecto()).isEqualTo("Sistema de riego");
        assertThat(evento.getVersion()).isEqualTo(3);
        assertThat(evento.getEstadoEntregable()).isEqualTo(EstadoEntregable.CANCELADO.getId());
        assertThat(evento.getEstadoEntregableNombre()).isEqualTo("Cancelado");
        assertThat(evento.getEstudiantes()).containsExactlyElementsOf(estudiantes);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }

    @Test
    void debeCopiarEstudiantesDeFormaInmutable_cuandoLaListaOrigenCambiaOEsNula() {
        // Arrange
        var contacto = new ContactoEstudiante(UtilUUID.generarNuevoUUID(), "Ana", "ana@uco.edu.co");
        var origen = new ArrayList<>(List.of(contacto));
        var evento = new EntregableProyectoGradoCanceladoEvent(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "P", 1, origen);
        var sinEstudiantes = new EntregableProyectoGradoCanceladoEvent(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "P", 1, null);

        // Act
        origen.clear();

        // Assert
        assertThat(evento.getEstudiantes()).hasSize(1);
        assertThat(sinEstudiantes.getEstudiantes()).isEmpty();
        assertThatThrownBy(() -> evento.getEstudiantes().clear()).isInstanceOf(UnsupportedOperationException.class);
    }
}
