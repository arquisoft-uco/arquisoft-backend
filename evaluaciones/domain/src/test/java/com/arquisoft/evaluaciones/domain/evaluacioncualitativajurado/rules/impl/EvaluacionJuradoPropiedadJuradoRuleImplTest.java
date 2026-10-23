package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionJuradoPropiedadJuradoRuleImplTest {

    private final EvaluacionJuradoPropiedadJuradoRuleImpl regla = new EvaluacionJuradoPropiedadJuradoRuleImpl();

    @Test
    void debeNoLanzar_cuandoJuradoEsElAsignado() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var propiedad = new PropiedadEvaluacionJurado(UtilUUID.generarNuevoUUID(), jurado, jurado);

        // Act & Assert
        assertThatCode(() -> regla.validar(propiedad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoPerteneceJurado_cuandoJuradoEsOtro() {
        // Arrange
        var propiedad = new PropiedadEvaluacionJurado(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(propiedad))
                .isInstanceOfSatisfying(EvaluacionJuradoNoPerteneceJuradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.EvaluacionCualitativaJurado.NO_PERTENECE_JURADO));
    }
}
