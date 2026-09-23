package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator.impl.RemoverItemCuantitativoJuradoValidatorImpl;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.ItemCuantitativoJuradoEnUsoException;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.ItemCuantitativoJuradoNoEncontradoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverItemCuantitativoJuradoValidatorTest {

    private final RemoverItemCuantitativoJuradoValidatorImpl validator =
            new RemoverItemCuantitativoJuradoValidatorImpl();

    @Test
    void debeNoLanzar_cuandoItemExisteYNoEstaEnUso() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(itemCuantitativoJurado, true, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoItemNoExiste() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(itemCuantitativoJurado, false, false))
                .isInstanceOfSatisfying(
                        ItemCuantitativoJuradoNoEncontradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCuantitativoJurado.ITEM_NO_ENCONTRADO));
    }

    @Test
    void debeLanzarEnUso_cuandoItemExisteYEstaEnUso() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(itemCuantitativoJurado, true, true))
                .isInstanceOfSatisfying(
                        ItemCuantitativoJuradoEnUsoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCuantitativoJurado.ITEM_EN_USO));
    }

    @Test
    void debeLanzarNoEncontradoPrimero_cuandoNoExisteYEnUsoEsVerdadero() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(itemCuantitativoJurado, false, true))
                .isInstanceOfSatisfying(
                        ItemCuantitativoJuradoNoEncontradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCuantitativoJurado.ITEM_NO_ENCONTRADO));
    }
}
