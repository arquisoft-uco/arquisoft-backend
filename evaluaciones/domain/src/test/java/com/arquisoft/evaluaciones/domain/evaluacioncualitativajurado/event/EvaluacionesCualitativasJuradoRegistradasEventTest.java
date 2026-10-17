package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.event;

import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionesCualitativasJuradoRegistradasEventTest {

    @Test
    void debeExponerElEntregable_cuandoSeConstruyeElEvento() {
        // Arrange
        UUID entregable = UUID.randomUUID();

        // Act
        var evento = new EvaluacionesCualitativasJuradoRegistradasEvent(entregable);

        // Assert
        assertThat(evento.getEntregableId()).isEqualTo(entregable);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Evaluaciones.EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS);
    }
}
