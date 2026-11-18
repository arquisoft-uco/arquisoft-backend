package com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.NombreItemCualitativoAsesorDuplicadoException;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.model.DisponibilidadNombreItemCualitativoAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NombreItemCualitativoAsesorUnicoRuleImplTest {

    private final NombreItemCualitativoAsesorUnicoRuleImpl regla =
            new NombreItemCualitativoAsesorUnicoRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoNombreYaExiste() {
        // Arrange
        var disponibilidad = new DisponibilidadNombreItemCualitativoAsesor("Claridad", true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOfSatisfying(
                        NombreItemCualitativoAsesorDuplicadoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCualitativoAsesor.NOMBRE_DUPLICADO));
    }

    @Test
    void noDebeLanzar_cuandoNombreDisponible() {
        // Arrange
        var disponibilidad = new DisponibilidadNombreItemCualitativoAsesor("Claridad", false);

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
