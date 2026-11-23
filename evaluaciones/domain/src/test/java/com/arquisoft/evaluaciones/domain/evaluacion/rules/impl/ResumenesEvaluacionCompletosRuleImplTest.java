package com.arquisoft.evaluaciones.domain.evaluacion.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacion.ResumenEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.ResumenesEvaluacionIncompletosException;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ResumenesEvaluacion;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResumenesEvaluacionCompletosRuleImplTest {

    private static final ResumenEvaluacionDomain CALCULADO = ResumenEvaluacionDomain.reconstruir(
            BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);

    private final ResumenesEvaluacionCompletosRuleImpl regla = new ResumenesEvaluacionCompletosRuleImpl();

    @Test
    void debePermitirFlujo_cuandoElAsesorTieneNotaYHayUnResumenPorJuradoEvaluado() {
        // Arrange
        var entrada = new ResumenesEvaluacion(
                UtilUUID.generarNuevoUUID(), CALCULADO, List.of(CALCULADO, CALCULADO), 2);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarConDetalle_cuandoElResumenDelAsesorNoEstaCalculado() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entrada = new ResumenesEvaluacion(evaluacion, ResumenEvaluacionDomain.VACIO, List.of(CALCULADO), 1);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(ResumenesEvaluacionIncompletosException.class, exception -> {
                    assertThat(exception.getCodigoError()).isEqualTo(EvaluacionesCodes.Evaluacion.RESUMENES_INCOMPLETOS);
                    assertThat(exception.getMessage()).contains(evaluacion.toString());
                    assertThat(exception.getError().getMessage()).doesNotContain(evaluacion.toString());
                });
    }

    @Test
    void debeLanzar_cuandoFaltanResumenesDeJuradosEvaluados() {
        // Arrange
        var entrada = new ResumenesEvaluacion(UtilUUID.generarNuevoUUID(), CALCULADO, List.of(CALCULADO), 2);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(ResumenesEvaluacionIncompletosException.class, exception ->
                        assertThat(exception.getCodigoError()).isEqualTo(EvaluacionesCodes.Evaluacion.RESUMENES_INCOMPLETOS));
    }
}
