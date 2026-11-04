package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionesCuantitativasAsesorRegistradasEventTest {

    @Test
    void debeExponerEntregableTemaYTipo_cuandoSeConstruyeElEvento() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new EvaluacionesCuantitativasAsesorRegistradasEvent(entregable);

        // Assert
        assertThat(evento.getEntregableId()).isEqualTo(entregable);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
        assertThat(evento.getTemaEvento())
                .isEqualTo(EventTopics.Evaluaciones.EVALUACIONES_CUANTITATIVAS_ASESOR_REGISTRADAS);
        assertThat(evento.getTipoEvento()).isEqualTo(EvaluacionesCuantitativasAsesorRegistradasEvent.EVENT_TYPE);
    }
}
