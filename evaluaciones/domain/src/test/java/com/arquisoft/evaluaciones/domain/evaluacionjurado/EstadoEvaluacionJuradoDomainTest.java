package com.arquisoft.evaluaciones.domain.evaluacionjurado;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoEvaluacionJuradoDomainTest {

    @Test
    void debeReconstruirConSusDatos_cuandoLaEvaluacionJuradoExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();

        // Act
        var estado = EstadoEvaluacionJuradoDomain.reconstruir(id, jurado, EstadoEvaluacion.EN_PROGRESO);

        // Assert
        assertThat(estado.esVacio()).isFalse();
        assertThat(estado.getId()).isEqualTo(id);
        assertThat(estado.getJurado()).isEqualTo(jurado);
        assertThat(estado.getEstado()).isEqualTo(EstadoEvaluacion.EN_PROGRESO);
    }

    @Test
    void debeExponerValoresPorDefecto_cuandoEsVacio() {
        // Act
        var vacio = EstadoEvaluacionJuradoDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(vacio.getId()).isEqualTo(UtilUUID.obtenerUUIDPorDefecto());
        assertThat(vacio.getJurado()).isEqualTo(UtilUUID.obtenerUUIDPorDefecto());
        assertThat(vacio.getEstado()).isEqualTo(EstadoEvaluacion.VACIO);
    }
}
