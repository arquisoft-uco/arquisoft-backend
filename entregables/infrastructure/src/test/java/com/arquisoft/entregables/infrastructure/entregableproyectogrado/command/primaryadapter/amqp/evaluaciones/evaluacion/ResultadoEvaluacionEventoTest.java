package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.evaluaciones.evaluacion;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.util.UtilTexto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResultadoEvaluacionEventoTest {

    @Test
    void debeTraducirCadaResultadoASuEstadoDelEntregable_cuandoElResultadoEsConocido() {
        // Act & Assert
        assertThat(ResultadoEvaluacionEvento.estadoEntregableDe("APROBADA")).isEqualTo("APROBADO");
        assertThat(ResultadoEvaluacionEvento.estadoEntregableDe("APROBADA_CON_OBSERVACIONES"))
                .isEqualTo("APROBADO_CON_OBSERVACIONES");
        assertThat(ResultadoEvaluacionEvento.estadoEntregableDe("RECHAZADA")).isEqualTo("RECHAZADO");
    }

    @Test
    void debeDevolverTextoVacio_cuandoElResultadoEsDesconocidoNuloOEnBlanco() {
        // Act & Assert
        assertThat(ResultadoEvaluacionEvento.estadoEntregableDe("PENDIENTE")).isEqualTo(UtilTexto.VACIO);
        assertThat(ResultadoEvaluacionEvento.estadoEntregableDe(null)).isEqualTo(UtilTexto.VACIO);
        assertThat(ResultadoEvaluacionEvento.estadoEntregableDe(" ")).isEqualTo(UtilTexto.VACIO);
    }

    @Test
    void debeTraducirSoloAEstadosQueExistenEnElEnumDelEntregable_cuandoSeRecorreCadaResultado() {
        // Act & Assert
        for (var resultado : ResultadoEvaluacionEvento.values()) {
            assertThat(EstadoEntregable.esValido(resultado.getEstadoEntregable()))
                    .as(resultado.name())
                    .isTrue();
        }
    }
}
