package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RemocionItemCuantitativoAsesorDomainTest {

    @Test
    void debeCrearRemocion_cuandoItemEsValido() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act
        var remocion = RemocionItemCuantitativoAsesorDomain.crear(item);

        // Assert
        assertThat(remocion.getItemCuantitativoAsesor()).isEqualTo(item);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoItemEsNulo() {
        // Arrange
        UUID item = null;

        // Act & Assert
        assertThatThrownBy(() -> RemocionItemCuantitativoAsesorDomain.crear(item))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ItemCuantitativoAsesor.ITEM,
                                        EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_ID_REQUERIDO)));
    }
}
