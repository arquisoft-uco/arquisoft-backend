package com.arquisoft.evaluaciones.domain.itemcualitativojurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ModificacionItemCualitativoJuradoDomainTest {

    @Test
    void debeCrearYAplicarTrim_cuandoDatosValidos() {
        // Arrange
        UUID itemCualitativoJurado = UUID.randomUUID();
        String descripcion = "  Evalúa la claridad de la exposición  ";

        // Act
        ModificacionItemCualitativoJuradoDomain modificacion =
                ModificacionItemCualitativoJuradoDomain.crear(itemCualitativoJurado, descripcion);

        // Assert
        assertThat(modificacion.getItemCualitativoJurado()).isEqualTo(itemCualitativoJurado);
        assertThat(modificacion.getDescripcion()).isEqualTo("Evalúa la claridad de la exposición");
    }

    @Test
    void debeAcumularErrores_cuandoIdEsNuloYDescripcionEnBlanco() {
        // Arrange
        UUID itemCualitativoJurado = null;
        String descripcion = "   ";

        // Act & Assert
        assertThatThrownBy(() -> ModificacionItemCualitativoJuradoDomain.crear(
                itemCualitativoJurado, descripcion))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(
                                                EvaluacionesFields.ItemCualitativoJurado.ITEM,
                                                EvaluacionesCodes.ItemCualitativoJurado.ITEM_ID_REQUERIDO),
                                        tuple(
                                                EvaluacionesFields.ItemCualitativoJurado.DESCRIPCION,
                                                EvaluacionesCodes.ItemCualitativoJurado
                                                        .DESCRIPCION_REQUERIDA)));
    }

    @Test
    void debeRechazarDescripcion_cuandoSuperaLongitudMaxima() {
        // Arrange
        UUID itemCualitativoJurado = UUID.randomUUID();
        String descripcion = "d".repeat(301);

        // Act & Assert
        assertThatThrownBy(() -> ModificacionItemCualitativoJuradoDomain.crear(
                itemCualitativoJurado, descripcion))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ItemCualitativoJurado.DESCRIPCION,
                                        EvaluacionesCodes.ItemCualitativoJurado
                                                .DESCRIPCION_DEMASIADO_LARGA)));
    }
}
