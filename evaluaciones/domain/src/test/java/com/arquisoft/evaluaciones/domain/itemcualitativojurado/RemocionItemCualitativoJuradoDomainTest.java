package com.arquisoft.evaluaciones.domain.itemcualitativojurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RemocionItemCualitativoJuradoDomainTest {

    @Test
    void debeCrearRemocion_cuandoItemEsValido() {
        // Arrange
        var itemCualitativoJurado = UUID.randomUUID();

        // Act
        var remocion = RemocionItemCualitativoJuradoDomain.crear(itemCualitativoJurado);

        // Assert
        assertThat(remocion.getItemCualitativoJurado()).isEqualTo(itemCualitativoJurado);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoItemEsNulo() {
        // Arrange
        UUID itemCualitativoJurado = null;

        // Act & Assert
        assertThatThrownBy(() -> RemocionItemCualitativoJuradoDomain.crear(itemCualitativoJurado))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ItemCualitativoJurado.ITEM,
                                        EvaluacionesCodes.ItemCualitativoJurado.ITEM_ID_REQUERIDO)));
    }
}
