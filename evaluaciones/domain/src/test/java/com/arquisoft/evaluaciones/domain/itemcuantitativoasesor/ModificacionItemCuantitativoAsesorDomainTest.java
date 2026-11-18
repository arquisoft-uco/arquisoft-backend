package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ModificacionItemCuantitativoAsesorDomainTest {

    @Test
    void debeCrearYRecortarDescripcion_cuandoDatosValidos() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act
        var modificacion = ModificacionItemCuantitativoAsesorDomain.crear(item, "  Descripción ampliada  ");

        // Assert
        assertThat(modificacion.getItemCuantitativoAsesor()).isEqualTo(item);
        assertThat(modificacion.getDescripcion()).isEqualTo("Descripción ampliada");
    }

    @Test
    void debeAcumularFieldErrors_cuandoItemNuloYDescripcionVacia() {
        // Act & Assert
        assertThatThrownBy(() -> ModificacionItemCuantitativoAsesorDomain.crear(null, "   "))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.ItemCuantitativoAsesor.ITEM,
                                                EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_ID_REQUERIDO),
                                        tuple(EvaluacionesFields.ItemCuantitativoAsesor.DESCRIPCION,
                                                EvaluacionesCodes.ItemCuantitativoAsesor.DESCRIPCION_REQUERIDA)));
    }

    @Test
    void debeAceptarLimiteYRechazarExceso_cuandoDescripcionEsLarga() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> ModificacionItemCuantitativoAsesorDomain.crear(item, "d".repeat(300)))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> ModificacionItemCuantitativoAsesorDomain.crear(item, "d".repeat(301)))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.ItemCuantitativoAsesor.DESCRIPCION_DEMASIADO_LARGA));
    }
}
