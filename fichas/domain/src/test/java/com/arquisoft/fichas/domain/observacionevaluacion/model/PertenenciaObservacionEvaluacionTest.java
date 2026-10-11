package com.arquisoft.fichas.domain.observacionevaluacion.model;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PertenenciaObservacionEvaluacionTest {

    @Test
    void debeSerVacioSoloElCentinela_cuandoOtraInstanciaTieneLosMismosValores() {
        // Arrange — esVacio compara identidad, no valores
        var igualEnValores = new PertenenciaObservacionEvaluacion(
                UtilUUID.obtenerUUIDPorDefecto(), false, EstadoEvaluacion.VACIO);

        // Act
        var centinelaEsVacio = PertenenciaObservacionEvaluacion.VACIO.esVacio();
        var copiaEsVacia = igualEnValores.esVacio();

        // Assert
        assertThat(centinelaEsVacio).isTrue();
        assertThat(copiaEsVacia).isFalse();
        assertThat(igualEnValores).isEqualTo(PertenenciaObservacionEvaluacion.VACIO);
    }
}
