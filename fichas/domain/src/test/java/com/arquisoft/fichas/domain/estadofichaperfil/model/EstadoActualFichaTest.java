package com.arquisoft.fichas.domain.estadofichaperfil.model;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoActualFichaTest {

    @Test
    void debeSerVacio_cuandoEsElCentinela() {
        // Arrange
        var real = new EstadoActualFicha(UtilUUID.generarNuevoUUID(), EstadoFicha.DISPONIBLE_PARA_EVALUACION);

        // Act & Assert
        assertThat(EstadoActualFicha.VACIO.esVacio()).isTrue();
        assertThat(EstadoActualFicha.VACIO.fichaPerfil()).isEqualTo(UtilUUID.obtenerUUIDPorDefecto());
        assertThat(EstadoActualFicha.VACIO.estadoActual()).isEqualTo(EstadoFicha.VACIO);
        assertThat(real.esVacio()).isFalse();
    }
}
