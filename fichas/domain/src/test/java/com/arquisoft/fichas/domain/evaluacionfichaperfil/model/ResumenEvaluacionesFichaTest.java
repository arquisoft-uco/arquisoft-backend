package com.arquisoft.fichas.domain.evaluacionfichaperfil.model;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResumenEvaluacionesFichaTest {

    @Test
    void debeSumarPorClasificacionDelEstado_cuandoHayConteosDeTodosLosEstados() {
        // Arrange
        var resumen = new ResumenEvaluacionesFicha(UUID.randomUUID(), List.of(
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.EN_EVALUACION, 4, 1),
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.APROBADA, 2, 0),
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.APROBADA_CON_OBSERVACIONES, 1, 1),
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.NO_APROBADA, 3, 0),
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.DESCARTADA, 5, 5)));

        // Act
        var finalizadas = resumen.finalizadas();
        var aprobatorias = resumen.aprobatorias();
        var tieneObservaciones = resumen.tieneObservacionesVigentes();

        // Assert
        assertThat(finalizadas).isEqualTo(6);
        assertThat(aprobatorias).isEqualTo(3);
        assertThat(tieneObservaciones).isTrue();
    }

    @Test
    void debeIgnorarObservacionesDeDescartadas_cuandoSonLasUnicasQueTienenObservaciones() {
        // Arrange
        var resumen = new ResumenEvaluacionesFicha(UUID.randomUUID(), List.of(
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.APROBADA, 1, 0),
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.DESCARTADA, 2, 2)));

        // Act
        var tieneObservaciones = resumen.tieneObservacionesVigentes();

        // Assert
        assertThat(tieneObservaciones).isFalse();
    }

    @Test
    void debeDevolverCeros_cuandoLaListaDeConteosLlegaNula() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();

        // Act
        var resumen = new ResumenEvaluacionesFicha(fichaPerfil, null);

        // Assert
        assertThat(resumen.conteos()).isEmpty();
        assertThat(resumen.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(resumen.finalizadas()).isZero();
        assertThat(resumen.aprobatorias()).isZero();
        assertThat(resumen.tieneObservacionesVigentes()).isFalse();
    }
}
