package com.arquisoft.artefactos.domain.revisionasesor.model;

import com.arquisoft.artefactos.domain.revisionasesor.exception.EstadoRevisionAsesorNoEncontradoException;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoRevisionAsesorTest {

    @Test
    void debeDevolverConstante_cuandoIdValido() {
        // Act
        var estado = EstadoRevisionAsesor.desde("EN_PROGRESO");

        // Assert
        assertThat(estado).isEqualTo(EstadoRevisionAsesor.EN_PROGRESO);
        assertThat(estado.getId()).isEqualTo("EN_PROGRESO");
        assertThat(estado.getNombre()).isEqualTo("En progreso");
    }

    @Test
    void debeLanzarNoEncontrado_cuandoIdDesconocido() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoRevisionAsesor.desde("INEXISTENTE"))
                .isInstanceOfSatisfying(EstadoRevisionAsesorNoEncontradoException.class,
                        ex -> assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.ESTADO_INVALIDO));
    }

    @Test
    void esValido_debeExcluirVacioYDesconocidos() {
        // Act & Assert
        assertThat(EstadoRevisionAsesor.esValido("RESUELTA")).isTrue();
        assertThat(EstadoRevisionAsesor.esValido("VACIO")).isFalse();
        assertThat(EstadoRevisionAsesor.esValido("XYZ")).isFalse();
    }

    @Test
    void esResuelta_debeSerVerdaderoSoloParaResuelta() {
        // Act & Assert
        assertThat(EstadoRevisionAsesor.RESUELTA.esResuelta()).isTrue();
        assertThat(EstadoRevisionAsesor.PENDIENTE.esResuelta()).isFalse();
        assertThat(EstadoRevisionAsesor.EN_PROGRESO.esResuelta()).isFalse();
        assertThat(EstadoRevisionAsesor.CERRADA.esResuelta()).isFalse();
    }
}
