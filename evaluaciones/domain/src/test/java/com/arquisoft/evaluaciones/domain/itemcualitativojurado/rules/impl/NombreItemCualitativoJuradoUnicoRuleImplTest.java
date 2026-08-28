package com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.NombreItemCualitativoJuradoDuplicadoException;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.model.DisponibilidadNombreItemCualitativoJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NombreItemCualitativoJuradoUnicoRuleImplTest {

    private final NombreItemCualitativoJuradoUnicoRuleImpl regla =
            new NombreItemCualitativoJuradoUnicoRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoNombreYaExiste() {
        // Arrange
        var disponibilidad = new DisponibilidadNombreItemCualitativoJurado("Claridad", true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOfSatisfying(
                        NombreItemCualitativoJuradoDuplicadoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCualitativoJurado.NOMBRE_DUPLICADO));
    }

    @Test
    void debePermitirRegistro_cuandoNombreEstaDisponible() {
        // Arrange
        var disponibilidad = new DisponibilidadNombreItemCualitativoJurado("Claridad", false);

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
