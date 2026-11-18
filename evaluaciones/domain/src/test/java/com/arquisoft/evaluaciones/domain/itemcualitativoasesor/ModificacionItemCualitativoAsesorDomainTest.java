package com.arquisoft.evaluaciones.domain.itemcualitativoasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ModificacionItemCualitativoAsesorDomainTest {

    @Test
    void debeCrearYRecortarDescripcion_cuandoDatosValidos() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act
        var modificacion = ModificacionItemCualitativoAsesorDomain.crear(item, "  Descripción ampliada  ");

        // Assert
        assertThat(modificacion.getItemCualitativoAsesor()).isEqualTo(item);
        assertThat(modificacion.getDescripcion()).isEqualTo("Descripción ampliada");
    }

    @Test
    void debeAcumularFieldErrors_cuandoItemNuloYDescripcionVacia() {
        // Act & Assert
        assertThatThrownBy(() -> ModificacionItemCualitativoAsesorDomain.crear(null, "   "))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.ItemCualitativoAsesor.ITEM,
                                                EvaluacionesCodes.ItemCualitativoAsesor.ITEM_ID_REQUERIDO),
                                        tuple(EvaluacionesFields.ItemCualitativoAsesor.DESCRIPCION,
                                                EvaluacionesCodes.ItemCualitativoAsesor.DESCRIPCION_REQUERIDA)));
    }

    @Test
    void debeAceptarLimiteYRechazarExceso_cuandoDescripcionEsLarga() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> ModificacionItemCualitativoAsesorDomain.crear(item, "d".repeat(300)))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> ModificacionItemCualitativoAsesorDomain.crear(item, "d".repeat(301)))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.ItemCualitativoAsesor.DESCRIPCION_DEMASIADO_LARGA));
    }
}
