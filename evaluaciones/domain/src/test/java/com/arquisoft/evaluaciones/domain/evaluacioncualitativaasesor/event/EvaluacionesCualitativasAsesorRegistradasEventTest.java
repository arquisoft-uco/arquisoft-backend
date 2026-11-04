package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionesCualitativasAsesorRegistradasEventTest {

    @Test
    void debeExponerEntregableTemaYTipo_cuandoSeConstruyeElEvento() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new EvaluacionesCualitativasAsesorRegistradasEvent(entregable);

        // Assert
        assertThat(evento.getEntregableId()).isEqualTo(entregable);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
        assertThat(evento.getTemaEvento())
                .isEqualTo(EventTopics.Evaluaciones.EVALUACIONES_CUALITATIVAS_ASESOR_REGISTRADAS);
        assertThat(evento.getTipoEvento()).isEqualTo(EvaluacionesCualitativasAsesorRegistradasEvent.EVENT_TYPE);
    }
}
