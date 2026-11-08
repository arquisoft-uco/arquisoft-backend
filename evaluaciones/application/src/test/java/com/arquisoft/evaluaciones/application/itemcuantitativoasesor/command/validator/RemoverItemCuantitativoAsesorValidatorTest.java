package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.impl.RemoverItemCuantitativoAsesorValidatorImpl;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.ItemCuantitativoAsesorEnUsoException;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.ItemCuantitativoAsesorNoEncontradoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverItemCuantitativoAsesorValidatorTest {

    private final RemoverItemCuantitativoAsesorValidatorImpl validator =
            new RemoverItemCuantitativoAsesorValidatorImpl();

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
                .isInstanceOfSatisfying(ItemCuantitativoAsesorNoEncontradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_NO_ENCONTRADO));
    }

    @Test
    void debeLanzarEnUso_cuandoItemExisteYEstaEnUso() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(item, true, true))
                .isInstanceOfSatisfying(ItemCuantitativoAsesorEnUsoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_EN_USO));
    }

    @Test
    void debeLanzarNoEncontradoPrimero_cuandoNoExisteYEnUsoEsVerdadero() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(item, false, true))
                .isInstanceOf(ItemCuantitativoAsesorNoEncontradoException.class);
    }
}
