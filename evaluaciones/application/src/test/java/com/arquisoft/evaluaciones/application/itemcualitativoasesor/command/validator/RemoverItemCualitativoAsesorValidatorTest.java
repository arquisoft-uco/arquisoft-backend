package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.impl.RemoverItemCualitativoAsesorValidatorImpl;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.ItemCualitativoAsesorEnUsoException;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.ItemCualitativoAsesorNoEncontradoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverItemCualitativoAsesorValidatorTest {

    private final RemoverItemCualitativoAsesorValidatorImpl validator =
            new RemoverItemCualitativoAsesorValidatorImpl();

    @Test
    void noDebeLanzar_cuandoItemExisteYNoEstaEnUso() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(item, true, false)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoItemNoExiste() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(item, false, false))
                .isInstanceOfSatisfying(ItemCualitativoAsesorNoEncontradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCualitativoAsesor.ITEM_NO_ENCONTRADO));
    }

    @Test
    void debeLanzarEnUso_cuandoItemExisteYEstaEnUso() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(item, true, true))
                .isInstanceOfSatisfying(ItemCualitativoAsesorEnUsoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCualitativoAsesor.ITEM_EN_USO));
    }

    @Test
    void debeLanzarNoEncontradoPrimero_cuandoNoExisteYEnUsoEsVerdadero() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(item, false, true))
                .isInstanceOf(ItemCualitativoAsesorNoEncontradoException.class);
    }
}
