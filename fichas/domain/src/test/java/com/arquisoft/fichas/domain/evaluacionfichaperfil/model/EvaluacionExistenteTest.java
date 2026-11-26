package com.arquisoft.fichas.domain.evaluacionfichaperfil.model;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionExistenteTest {

    @Test
    void debeSerVacio_cuandoEsElCentinela() {
        // Arrange
        var real = new EvaluacionExistente(UtilUUID.generarNuevoUUID(), EstadoEvaluacion.DESCARTADA);

        // Act & Assert
        assertThat(EvaluacionExistente.VACIO.esVacio()).isTrue();
        assertThat(EvaluacionExistente.VACIO.evaluacion()).isEqualTo(UtilUUID.obtenerUUIDPorDefecto());
        assertThat(EvaluacionExistente.VACIO.estadoActual()).isEqualTo(EstadoEvaluacion.VACIO);
        assertThat(real.esVacio()).isFalse();
    }
}
