package com.arquisoft.evaluaciones.domain.itemcuantitativojurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ModificacionItemCuantitativoJuradoDomainTest {

    @Test
    void debeCrearYAplicarTrim_cuandoDatosValidos() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();
        var descripcion = "  Evalúa la calidad técnica de la exposición  ";

        // Act
        var modificacion = ModificacionItemCuantitativoJuradoDomain.crear(
                itemCuantitativoJurado, descripcion);

        // Assert
        assertThat(modificacion.getItemCuantitativoJurado()).isEqualTo(itemCuantitativoJurado);
        assertThat(modificacion.getDescripcion())
                .isEqualTo("Evalúa la calidad técnica de la exposición");
    }

    @Test
    void debeAcumularErrores_cuandoIdEsNuloYDescripcionEnBlanco() {
        // Arrange
        UUID itemCuantitativoJurado = null;
        var descripcion = "   ";

        // Act & Assert
        assertThatThrownBy(() -> ModificacionItemCuantitativoJuradoDomain.crear(
                itemCuantitativoJurado, descripcion))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(
                                                EvaluacionesFields.ItemCuantitativoJurado.ITEM,
                                                EvaluacionesCodes.ItemCuantitativoJurado
                                                        .ITEM_ID_REQUERIDO),
                                        tuple(
                                                EvaluacionesFields.ItemCuantitativoJurado.DESCRIPCION,
                                                EvaluacionesCodes.ItemCuantitativoJurado
                                                        .DESCRIPCION_REQUERIDA)));
    }

    @Test
    void debeRechazarDescripcion_cuandoSuperaLongitudMaxima() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();
        var descripcion = "d".repeat(301);

        // Act & Assert
        assertThatThrownBy(() -> ModificacionItemCuantitativoJuradoDomain.crear(
                itemCuantitativoJurado, descripcion))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ItemCuantitativoJurado.DESCRIPCION,
                                        EvaluacionesCodes.ItemCuantitativoJurado
                                                .DESCRIPCION_DEMASIADO_LARGA)));
    }
}
