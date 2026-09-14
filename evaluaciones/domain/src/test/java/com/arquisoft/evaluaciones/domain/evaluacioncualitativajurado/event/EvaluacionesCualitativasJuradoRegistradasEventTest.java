package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.event;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.event.EvaluacionesCualitativasJuradoRegistradasEvent.DatosLoteRegistrado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ContactoEstudianteEvaluacion;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionesCualitativasJuradoRegistradasEventTest {

    @Test
    void debeExponerTodosLosDatosDelLoteYSusContactos_cuandoSeConstruyeElEvento() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID evaluacion = UUID.randomUUID();
        UUID entregable = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        var datos = new DatosLoteRegistrado(evaluacionJurado, evaluacion, entregable, jurado, "Proyecto X", 2, 3);
        var contacto = new ContactoEstudianteEvaluacion(estudiante, "estudiante@uco.edu.co");

        // Act
        var evento = new EvaluacionesCualitativasJuradoRegistradasEvent(datos, List.of(contacto));

        // Assert
        assertThat(evento.getEvaluacionJuradoId()).isEqualTo(evaluacionJurado);
        assertThat(evento.getEvaluacionId()).isEqualTo(evaluacion);
        assertThat(evento.getEntregableId()).isEqualTo(entregable);
        assertThat(evento.getJuradoId()).isEqualTo(jurado);
        assertThat(evento.getProyecto()).isEqualTo("Proyecto X");
        assertThat(evento.getVersionEntregable()).isEqualTo(2);
        assertThat(evento.getCantidad()).isEqualTo(3);
        assertThat(evento.getEstudiantes()).containsExactly(contacto);
        assertThat(evento.getEstudiantes().get(0).estudiante()).isEqualTo(estudiante);
        assertThat(evento.getEstudiantes().get(0).email()).isEqualTo("estudiante@uco.edu.co");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Evaluaciones.EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS);
    }

    @Test
    void debeSerInmutable_cuandoSeModificaLaListaOriginalDeContactos() {
        // Arrange
        var datos = new DatosLoteRegistrado(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "Proyecto X", 1, 1);
        var estudiantes = new java.util.ArrayList<ContactoEstudianteEvaluacion>();
        estudiantes.add(new ContactoEstudianteEvaluacion(UUID.randomUUID(), "a@uco.edu.co"));

        // Act
        var evento = new EvaluacionesCualitativasJuradoRegistradasEvent(datos, estudiantes);
        estudiantes.add(new ContactoEstudianteEvaluacion(UUID.randomUUID(), "b@uco.edu.co"));

        // Assert
        assertThat(evento.getEstudiantes()).hasSize(1);
    }
}
