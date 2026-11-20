package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.evaluaciones.domain.evaluacion.model.PesosFinalizacion;
import com.arquisoft.evaluaciones.domain.resultadoevaluacion.ResultadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinalizacionEvaluacionDomainTest {

    private static ResumenEvaluacionDomain resumen(String nota) {
        return ResumenEvaluacionDomain.reconstruir(BigDecimal.ONE, BigDecimal.ONE, new BigDecimal(nota));
    }

    private static ConsolidacionNotasEvaluacionDomain consolidacionConJuradoUnico(String notaFinalBuscada) {
        return ConsolidacionNotasEvaluacionDomain.calcular(
                UtilUUID.generarNuevoUUID(), resumen(notaFinalBuscada), List.of(resumen(notaFinalBuscada)),
                new PesosFinalizacion(BigDecimal.ONE, BigDecimal.ONE));
    }

    @ParameterizedTest
    @CsvSource({"2.99,RECHAZADA", "3.00,APROBADA", "3.01,APROBADA"})
    void debeResolverElVeredictoSobreLaNotaFinal_cuandoNoHayObservaciones(
            String nota, ResultadoEvaluacion esperado) {
        // Act
        var finalizacion = FinalizacionEvaluacionDomain.crear(consolidacionConJuradoUnico(nota), false);

        // Assert
        assertThat(finalizacion.getNotaFinal()).isEqualByComparingTo(nota);
        assertThat(finalizacion.getResultado()).isEqualTo(esperado);
    }

    @Test
    void debeRechazar_cuandoLaNotaEsMenorAlMinimoAunqueHayaObservaciones() {
        // Act
        var finalizacion = FinalizacionEvaluacionDomain.crear(consolidacionConJuradoUnico("2.99"), true);

        // Assert
        assertThat(finalizacion.getResultado()).isEqualTo(ResultadoEvaluacion.RECHAZADA);
    }

    @Test
    void debeAprobarConObservaciones_cuandoLaNotaAlcanzaElMinimoYHayObservaciones() {
        // Act
        var finalizacion = FinalizacionEvaluacionDomain.crear(consolidacionConJuradoUnico("3.00"), true);

        // Assert
        assertThat(finalizacion.getResultado()).isEqualTo(ResultadoEvaluacion.APROBADA_CON_OBSERVACIONES);
    }

    @Test
    void debeCalcularNotaFinalConFraccionesGuardadas_cuandoHayDosJurados() {
        // Arrange
        var consolidacion = ConsolidacionNotasEvaluacionDomain.calcular(
                UtilUUID.generarNuevoUUID(), resumen("4.00"), List.of(resumen("3.00"), resumen("3.00")),
                new PesosFinalizacion(BigDecimal.ONE, BigDecimal.ONE));

        // Act
        var finalizacion = FinalizacionEvaluacionDomain.crear(consolidacion, false);

        // Assert
        assertThat(finalizacion.getNotaFinal()).isEqualByComparingTo("3.33");
        assertThat(finalizacion.getEvaluacion()).isEqualTo(consolidacion.getEvaluacion());
        assertThat(finalizacion.getConsolidacion()).isSameAs(consolidacion);
    }

    @Test
    void debeLanzarValidacion_cuandoLaConsolidacionEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> FinalizacionEvaluacionDomain.crear(null, false))
                .isInstanceOf(DomainValidationException.class);
    }
}
