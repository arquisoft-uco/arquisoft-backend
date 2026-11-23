package com.arquisoft.evaluaciones.domain.evaluacion.event;

import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.model.ContactoCoordinador;
import com.arquisoft.evaluaciones.domain.entregable.EntregableDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.EvaluacionDomain;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionGeneradaEventTest {

    @Test
    void debeExponerTopicYCamposDelEntregable_cuandoSeConstruye() {
        // Arrange
        var entregable = EntregableDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Sistema de riego", 3);
        var evaluacion = EvaluacionDomain.crear(entregable.getId());
        var coordinador = new ContactoCoordinador(UtilUUID.generarNuevoUUID(), "Carlos Coord", "carlos@uco.edu.co");

        // Act
        var evento = new EvaluacionGeneradaEvent(evaluacion, entregable, coordinador);

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Evaluaciones.EVALUACION_GENERADA);
        assertThat(evento.getEvaluacionId()).isEqualTo(evaluacion.getId());
        assertThat(evento.getEntregableId()).isEqualTo(entregable.getId());
        assertThat(evento.getProyectoGradoId()).isEqualTo(entregable.getProyectoGrado());
        assertThat(evento.getNombreProyecto()).isEqualTo("Sistema de riego");
        assertThat(evento.getVersionEntregable()).isEqualTo(3);
        assertThat(evento.getCoordinador()).isEqualTo(coordinador);
    }
}
