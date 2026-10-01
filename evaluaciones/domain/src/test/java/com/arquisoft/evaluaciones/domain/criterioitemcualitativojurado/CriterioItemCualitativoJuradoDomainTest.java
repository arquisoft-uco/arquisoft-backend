package com.arquisoft.evaluaciones.domain.criterioitemcualitativojurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CriterioItemCualitativoJuradoDomainTest {

    @Test
    void debeCrearCriterioYAplicarTrim_cuandoDatosValidos() {
        // Arrange
        String nombre = "  Claridad conceptual  ";
        String descripcion = "  Evalúa la claridad de la exposición  ";

        // Act
        CriterioItemCualitativoJuradoDomain criterio =
                CriterioItemCualitativoJuradoDomain.crear(nombre, descripcion);

        // Assert
        assertThat(criterio.getId()).isNotNull();
        assertThat(criterio.getNombre()).isEqualTo("Claridad conceptual");
        assertThat(criterio.getDescripcion()).isEqualTo("Evalúa la claridad de la exposición");
    }

    @Test
    void debeAcumularErrores_cuandoCamposEstanEnBlanco() {
        // Arrange
        String nombre = "   ";
        String descripcion = null;

        // Act & Assert
        assertThatThrownBy(() -> CriterioItemCualitativoJuradoDomain.crear(nombre, descripcion))
                .isInstanceOfSatisfying(DomainValidationException.class, exception -> {
                    assertThat(exception.getValidationResult().getErrores())
                            .extracting(
                                    error -> error.campo(),
                                    error -> error.codigoError())
                            .containsExactlyInAnyOrder(
                                    org.assertj.core.groups.Tuple.tuple(
                                            EvaluacionesFields.CriterioItemCualitativoJurado.NOMBRE,
                                            EvaluacionesCodes.CriterioItemCualitativoJurado.NOMBRE_REQUERIDO),
                                    org.assertj.core.groups.Tuple.tuple(
                                            EvaluacionesFields.CriterioItemCualitativoJurado.DESCRIPCION,
                                            EvaluacionesCodes.CriterioItemCualitativoJurado.DESCRIPCION_REQUERIDA));
                });
    }

    @Test
    void debeAcumularErrores_cuandoCamposSuperanLongitudMaxima() {
        // Arrange
        String nombre = "n".repeat(101);
        String descripcion = "d".repeat(301);

        // Act & Assert
        assertThatThrownBy(() -> CriterioItemCualitativoJuradoDomain.crear(nombre, descripcion))
                .isInstanceOfSatisfying(DomainValidationException.class, exception -> {
                    assertThat(exception.getValidationResult().getErrores())
                            .extracting(error -> error.codigoError())
                            .containsExactlyInAnyOrder(
                                    EvaluacionesCodes.CriterioItemCualitativoJurado.NOMBRE_DEMASIADO_LARGO,
                                    EvaluacionesCodes.CriterioItemCualitativoJurado.DESCRIPCION_DEMASIADO_LARGA);
                });
    }

    @Test
    void debeReconstruirSinValidar_cuandoSeCargaDesdePersistencia() {
        // Arrange
        UUID id = UUID.randomUUID();
        String nombrePersistido = "";
        String descripcionPersistida = null;

        // Act
        CriterioItemCualitativoJuradoDomain criterio = CriterioItemCualitativoJuradoDomain.reconstruir(
                id, nombrePersistido, descripcionPersistida);

        // Assert
        assertThat(criterio.getId()).isEqualTo(id);
        assertThat(criterio.getNombre()).isEqualTo(nombrePersistido);
        assertThat(criterio.getDescripcion()).isNull();
    }
}
