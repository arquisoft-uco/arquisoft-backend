package com.arquisoft.evaluaciones.domain.itemcuantitativojurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RemocionItemCuantitativoJuradoDomainTest {

    @Test
    void debeCrearRemocion_cuandoItemEsValido() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();

        // Act
        var remocion = RemocionItemCuantitativoJuradoDomain.crear(itemCuantitativoJurado);

        // Assert
        assertThat(remocion.getItemCuantitativoJurado()).isEqualTo(itemCuantitativoJurado);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoItemEsNulo() {
        // Arrange
        UUID itemCuantitativoJurado = null;

        // Act & Assert
        assertThatThrownBy(() -> RemocionItemCuantitativoJuradoDomain.crear(itemCuantitativoJurado))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ItemCuantitativoJurado.ITEM,
                                        EvaluacionesCodes.ItemCuantitativoJurado.ITEM_ID_REQUERIDO)));
    }
}
