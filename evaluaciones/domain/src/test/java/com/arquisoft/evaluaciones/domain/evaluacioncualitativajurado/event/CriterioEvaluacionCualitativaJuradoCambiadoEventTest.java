package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CriterioEvaluacionCualitativaJuradoCambiadoEventTest {

    @Test
    void debeExponerTemaTipoYCampos_cuandoSeConstruye() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var criterioAnterior = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new CriterioEvaluacionCualitativaJuradoCambiadoEvent(
                evaluacion, evaluacionJurado, item, criterioAnterior, criterio, jurado);

        // Assert
        assertThat(evento.getTemaEvento())
                .isEqualTo(EventTopics.Evaluaciones.CRITERIO_EVALUACION_CUALITATIVA_JURADO_CAMBIADO);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
        assertThat(evento.getEvaluacionCualitativaJurado()).isEqualTo(evaluacion);
        assertThat(evento.getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(evento.getItem()).isEqualTo(item);
        assertThat(evento.getCriterioAnterior()).isEqualTo(criterioAnterior);
        assertThat(evento.getCriterio()).isEqualTo(criterio);
        assertThat(evento.getJurado()).isEqualTo(jurado);
    }
}
