package com.arquisoft.evaluaciones.domain.itemcualitativoasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemCualitativoAsesorDomainTest {

    @Test
    void debeCrearGenerarIdYRecortar_cuandoDatosValidos() {
        // Arrange
        var nombre = "  Claridad conceptual  ";
        var descripcion = "  Evalúa la claridad del trabajo  ";

        // Act
        var item = ItemCualitativoAsesorDomain.crear(nombre, descripcion);

        // Assert
        assertThat(item.getId()).isNotNull();
        assertThat(item.getNombre()).isEqualTo("Claridad conceptual");
        assertThat(item.getDescripcion()).isEqualTo("Evalúa la claridad del trabajo");
    }

    @Test
    void debeAcumularFieldErrors_cuandoNombreYDescripcionEstanVacios() {
        // Arrange
        var nombre = "   ";
        String descripcion = null;

        // Act & Assert
        assertThatThrownBy(() -> ItemCualitativoAsesorDomain.crear(nombre, descripcion))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        Tuple.tuple(
                                                EvaluacionesFields.ItemCualitativoAsesor.NOMBRE,
                                                EvaluacionesCodes.ItemCualitativoAsesor.NOMBRE_REQUERIDO),
                                        Tuple.tuple(
                                                EvaluacionesFields.ItemCualitativoAsesor.DESCRIPCION,
                                                EvaluacionesCodes.ItemCualitativoAsesor.DESCRIPCION_REQUERIDA)));
    }

    @Test
    void debeAcumularFieldErrors_cuandoNombreYDescripcionSuperanMaximo() {
        // Arrange
        var nombre = "n".repeat(101);
        var descripcion = "d".repeat(301);

        // Act & Assert
        assertThatThrownBy(() -> ItemCualitativoAsesorDomain.crear(nombre, descripcion))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.ItemCualitativoAsesor.NOMBRE_DEMASIADO_LARGO,
                                        EvaluacionesCodes.ItemCualitativoAsesor.DESCRIPCION_DEMASIADO_LARGA));
    }
}
