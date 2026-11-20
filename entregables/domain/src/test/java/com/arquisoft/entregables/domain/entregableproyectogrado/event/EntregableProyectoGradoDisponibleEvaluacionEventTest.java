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

class EntregableProyectoGradoDisponibleEvaluacionEventTest {

    @Test
    void debeExponerTopicCamposYEstadoFijo_cuandoSeConstruye() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudiantes = List.of(new ContactoEstudiante(UtilUUID.generarNuevoUUID(), "Ana", "ana@uco.edu.co"));

        // Act
        var evento = new EntregableProyectoGradoDisponibleEvaluacionEvent(entregable, proyecto, "Sistema de riego", 3,
                UtilUUID.generarNuevoUUID(), estudiantes);

        // Assert
        assertThat(evento.getTemaEvento())
                .isEqualTo(EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_DISPONIBLE_EVALUACION)
                .isEqualTo("entregables.entregable_proyecto_grado.disponible_evaluacion");
        assertThat(evento.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(evento.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(evento.getNombreProyecto()).isEqualTo("Sistema de riego");
        assertThat(evento.getVersion()).isEqualTo(3);
        assertThat(evento.getEstadoEntregable()).isEqualTo(EstadoEntregable.DISPONIBLE_EVALUACION.getId());
        assertThat(evento.getEstadoEntregableNombre()).isEqualTo("Disponible Evaluacion");
        assertThat(evento.getEstudiantes()).containsExactlyElementsOf(estudiantes);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }

    @Test
    void debeCopiarEstudiantesDeFormaInmutable_cuandoLaListaOrigenCambiaOEsNula() {
        // Arrange
        var contacto = new ContactoEstudiante(UtilUUID.generarNuevoUUID(), "Ana", "ana@uco.edu.co");
        var origen = new ArrayList<>(List.of(contacto));
        var evento = new EntregableProyectoGradoDisponibleEvaluacionEvent(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "P", 1, UtilUUID.generarNuevoUUID(), origen);
        var sinEstudiantes = new EntregableProyectoGradoDisponibleEvaluacionEvent(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "P", 1, UtilUUID.generarNuevoUUID(), null);

        // Act
        origen.clear();

        // Assert
        assertThat(evento.getEstudiantes()).hasSize(1);
        assertThat(sinEstudiantes.getEstudiantes()).isEmpty();
        assertThatThrownBy(() -> evento.getEstudiantes().clear()).isInstanceOf(UnsupportedOperationException.class);
    }
}
