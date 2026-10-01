package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.impl.RegistrarItemCualitativoJuradoValidatorImpl;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.ItemCualitativoJuradoDomain;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.NombreItemCualitativoJuradoDuplicadoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarItemCualitativoJuradoValidatorTest {

    private final RegistrarItemCualitativoJuradoValidatorImpl validator =
            new RegistrarItemCualitativoJuradoValidatorImpl();

    @Test
    void debeLanzarExcepcion_cuandoNombreYaExiste() {
        // Arrange
        ItemCualitativoJuradoDomain item =
                ItemCualitativoJuradoDomain.crear("Claridad", "Descripción");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(item, true))
                .isInstanceOfSatisfying(
                        NombreItemCualitativoJuradoDuplicadoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCualitativoJurado.NOMBRE_DUPLICADO));
    }
}
