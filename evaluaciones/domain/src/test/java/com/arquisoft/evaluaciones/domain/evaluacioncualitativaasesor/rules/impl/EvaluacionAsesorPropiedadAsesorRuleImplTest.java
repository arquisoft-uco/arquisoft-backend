package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionAsesorPropiedadAsesorRuleImplTest {

    private final EvaluacionAsesorPropiedadAsesorRuleImpl regla = new EvaluacionAsesorPropiedadAsesorRuleImpl();

    @Test
    void debePermitirFlujo_cuandoLaEvaluacionEsDelAsesorSolicitante() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var propiedad = new PropiedadEvaluacionAsesor(UtilUUID.generarNuevoUUID(), asesor, asesor);

        // Act & Assert
        assertThatCode(() -> regla.validar(propiedad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoLaEvaluacionEsDeOtroAsesor() {
        // Arrange
        var propiedad = new PropiedadEvaluacionAsesor(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(propiedad))
                .isInstanceOfSatisfying(EvaluacionAsesorNoPerteneceAsesorException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.EvaluacionCualitativaAsesor.NO_PERTENECE_ASESOR));
    }
}
