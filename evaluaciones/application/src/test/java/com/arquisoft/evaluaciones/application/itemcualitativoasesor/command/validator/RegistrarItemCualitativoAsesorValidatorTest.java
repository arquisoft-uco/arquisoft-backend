package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.impl.RegistrarItemCualitativoAsesorValidatorImpl;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.ItemCualitativoAsesorDomain;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.NombreItemCualitativoAsesorDuplicadoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarItemCualitativoAsesorValidatorTest {

    private final RegistrarItemCualitativoAsesorValidatorImpl validator =
            new RegistrarItemCualitativoAsesorValidatorImpl();

    @Test
    void debeLanzar_cuandoNombreYaExiste() {
        // Arrange
        var item = ItemCualitativoAsesorDomain.crear("Claridad", "Descripción");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(item, true))
                .isInstanceOfSatisfying(
                        NombreItemCualitativoAsesorDuplicadoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCualitativoAsesor.NOMBRE_DUPLICADO));
    }

    @Test
    void noDebeLanzar_cuandoNombreDisponible() {
        // Arrange
        var item = ItemCualitativoAsesorDomain.crear("Claridad", "Descripción");

        // Act & Assert
        assertThatCode(() -> validator.validar(item, false)).doesNotThrowAnyException();
    }
}
