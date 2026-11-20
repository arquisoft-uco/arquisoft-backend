package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.evaluaciones.domain.evaluacion.model.PesosFinalizacion;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ConsolidacionNotasEvaluacionDomainTest {

    private static final PesosFinalizacion PESOS_IGUALES = new PesosFinalizacion(BigDecimal.ONE, BigDecimal.ONE);

    private static ResumenEvaluacionDomain resumen(String nota) {
        return ResumenEvaluacionDomain.reconstruir(BigDecimal.ONE, BigDecimal.ONE, new BigDecimal(nota));
    }

    @ParameterizedTest
    @CsvSource({"1,0.5000,0.5000", "2,0.3333,0.6667", "3,0.2500,0.7500"})
    void debeRepartirFraccionesQueSumanUno_cuandoLosPesosSonIguales(
            int jurados, String pesoAsesor, String pesoJurados) {
        // Arrange
        var notas = Collections.nCopies(jurados, resumen("3.00"));

        // Act
        var consolidacion = ConsolidacionNotasEvaluacionDomain.calcular(
                UtilUUID.generarNuevoUUID(), resumen("4.00"), notas, PESOS_IGUALES);

        // Assert
        assertThat(consolidacion.getCantidadJurados()).isEqualTo(jurados);
        assertThat(consolidacion.getPesoAsesor()).isEqualByComparingTo(pesoAsesor);
        assertThat(consolidacion.getPesoJurados()).isEqualByComparingTo(pesoJurados);
        assertThat(consolidacion.getPesoAsesor().add(consolidacion.getPesoJurados())).isEqualByComparingTo("1");
    }

    @Test
    void debePromediarNotasDeJuradosYCopiarLaDelAsesor_cuandoHayVariosJurados() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();

        // Act
        var consolidacion = ConsolidacionNotasEvaluacionDomain.calcular(
                evaluacion, resumen("4.50"), List.of(resumen("3.00"), resumen("4.01")), PESOS_IGUALES);

        // Assert
        assertThat(consolidacion.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(consolidacion.getNotaAsesor()).isEqualByComparingTo("4.50");
        assertThat(consolidacion.getNotaJurados()).isEqualByComparingTo("3.51");
    }

    @Test
    void debeAplicarPesosDistintos_cuandoLaConfiguracionLosDiferencia() {
        // Arrange
        var pesos = new PesosFinalizacion(new BigDecimal("2"), new BigDecimal("1"));

        // Act
        var consolidacion = ConsolidacionNotasEvaluacionDomain.calcular(
                UtilUUID.generarNuevoUUID(), resumen("4.00"), List.of(resumen("3.00"), resumen("3.00")), pesos);

        // Assert
        assertThat(consolidacion.getPesoAsesor()).isEqualByComparingTo("0.5000");
        assertThat(consolidacion.getPesoJurados()).isEqualByComparingTo("0.5000");
    }

    @Test
    void debeAcumularErroresDePeso_cuandoLosPesosSonCeroONegativos() {
        // Arrange
        var pesos = new PesosFinalizacion(BigDecimal.ZERO, new BigDecimal("-1"));

        // Act & Assert
        assertThatThrownBy(() -> ConsolidacionNotasEvaluacionDomain.calcular(
                UtilUUID.generarNuevoUUID(), resumen("4.00"), List.of(resumen("3.00")), pesos))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .contains(
                                        tuple(EvaluacionesFields.ConsolidacionNotasEvaluacion.PESO_ASESOR,
                                                EvaluacionesCodes.ConsolidacionNotasEvaluacion.PESO_ASESOR_INVALIDO),
                                        tuple(EvaluacionesFields.ConsolidacionNotasEvaluacion.PESO_JURADO,
                                                EvaluacionesCodes.ConsolidacionNotasEvaluacion.PESO_JURADO_INVALIDO)));
    }

    @Test
    void debeAcumularErrores_cuandoNoHayJuradosNiNotaDelAsesor() {
        // Act & Assert
        assertThatThrownBy(() -> ConsolidacionNotasEvaluacionDomain.calcular(
                UtilUUID.generarNuevoUUID(), ResumenEvaluacionDomain.VACIO, List.of(), PESOS_IGUALES))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.ConsolidacionNotasEvaluacion.NOTA_ASESOR,
                                                EvaluacionesCodes.ConsolidacionNotasEvaluacion.NOTA_ASESOR_REQUERIDA),
                                        tuple(EvaluacionesFields.ConsolidacionNotasEvaluacion.NOTAS_JURADOS,
                                                EvaluacionesCodes.ConsolidacionNotasEvaluacion
                                                        .NOTAS_JURADOS_REQUERIDAS)));
    }

    @Test
    void debeRechazar_cuandoAlgunJuradoNoTieneNotaCalculada() {
        // Act & Assert
        assertThatThrownBy(() -> ConsolidacionNotasEvaluacionDomain.calcular(
                UtilUUID.generarNuevoUUID(), resumen("4.00"),
                List.of(resumen("3.00"), ResumenEvaluacionDomain.VACIO), PESOS_IGUALES))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(
                                        EvaluacionesCodes.ConsolidacionNotasEvaluacion.NOTAS_JURADOS_REQUERIDAS));
    }
}
