package com.arquisoft.artefactos.domain.observacion.model;

import com.arquisoft.artefactos.domain.observacion.exception.EstadoObservacionNoEncontradoException;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoObservacionTest {

    @Test
    void debeDevolverConstante_cuandoIdValido() {
        // Act
        var estado = EstadoObservacion.desde("EN_PROGRESO");

        // Assert
        assertThat(estado).isEqualTo(EstadoObservacion.EN_PROGRESO);
        assertThat(estado.getId()).isEqualTo("EN_PROGRESO");
        assertThat(estado.getNombre()).isEqualTo("En progreso");
    }

    @Test
    void debeLanzarNoEncontrado_cuandoIdDesconocidoOVacio() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoObservacion.desde("INEXISTENTE"))
                .isInstanceOfSatisfying(EstadoObservacionNoEncontradoException.class,
                        ex -> assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.Observacion.ESTADO_INVALIDO));
        assertThatThrownBy(() -> EstadoObservacion.desde("VACIO"))
                .isInstanceOf(EstadoObservacionNoEncontradoException.class);
    }

    @Test
    void esValido_debeExcluirVacioYDesconocidos() {
        // Act & Assert
        assertThat(EstadoObservacion.esValido("SOLUCIONADA")).isTrue();
        assertThat(EstadoObservacion.esValido("VACIO")).isFalse();
        assertThat(EstadoObservacion.esValido("XYZ")).isFalse();
    }

    @Test
    void debeResponderEsReabierta_soloParaReabierta() {
        // Act & Assert
        assertThat(EstadoObservacion.REABIERTA.esReabierta()).isTrue();
        assertThat(EstadoObservacion.CERRADA.esReabierta()).isFalse();
        assertThat(EstadoObservacion.SOLUCIONADA.esReabierta()).isFalse();
    }

    @Test
    void debeCoincidirConLasFilasDelMer_cuandoSeListanLosIds() {
        // Act
        var ids = Arrays.stream(EstadoObservacion.values())
                .filter(estado -> estado != EstadoObservacion.VACIO)
                .map(EstadoObservacion::getId)
                .toList();

        // Assert
        assertThat(ids).containsExactly("NUEVA", "EN_PROGRESO", "CERRADA", "SOLUCIONADA", "REABIERTA");
    }
}
