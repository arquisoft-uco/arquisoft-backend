package com.arquisoft.evaluaciones.domain.itemcualitativoasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RemocionItemCualitativoAsesorDomainTest {

    @Test
    void debeCrearRemocion_cuandoItemEsValido() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act
        var remocion = RemocionItemCualitativoAsesorDomain.crear(item);

        // Assert
        assertThat(remocion.getItemCualitativoAsesor()).isEqualTo(item);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoItemEsNulo() {
        // Arrange
        UUID item = null;

        // Act & Assert
        assertThatThrownBy(() -> RemocionItemCualitativoAsesorDomain.crear(item))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ItemCualitativoAsesor.ITEM,
                                        EvaluacionesCodes.ItemCualitativoAsesor.ITEM_ID_REQUERIDO)));
    }
}
