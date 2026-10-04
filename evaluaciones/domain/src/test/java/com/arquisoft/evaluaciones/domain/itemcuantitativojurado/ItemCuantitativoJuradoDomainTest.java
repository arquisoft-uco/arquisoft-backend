package com.arquisoft.evaluaciones.domain.itemcuantitativojurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemCuantitativoJuradoDomainTest {

    private static final UUID CATEGORIA = UUID.randomUUID();

    @Test
    void debeCrearItemYAplicarTrim_cuandoDatosValidos() {
        // Act
        ItemCuantitativoJuradoDomain item = ItemCuantitativoJuradoDomain.crear(
                "  Calidad técnica  ", "  Evalúa la calidad  ", CATEGORIA, 100);

        // Assert
        assertThat(item.getId()).isNotNull();
        assertThat(item.getNombre()).isEqualTo("Calidad técnica");
        assertThat(item.getDescripcion()).isEqualTo("Evalúa la calidad");
        assertThat(item.getCategoria()).isEqualTo(CATEGORIA);
        assertThat(item.getValor()).isEqualTo(100);
    }

    @Test
    void debeAcumularErrores_cuandoCamposRequeridosSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> ItemCuantitativoJuradoDomain.crear(" ", null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.ItemCuantitativoJurado.NOMBRE_REQUERIDO,
                                        EvaluacionesCodes.ItemCuantitativoJurado.DESCRIPCION_REQUERIDA,
                                        EvaluacionesCodes.ItemCuantitativoJurado.CATEGORIA_REQUERIDA,
                                        EvaluacionesCodes.ItemCuantitativoJurado.VALOR_REQUERIDO));
    }

    @Test
    void debeAcumularErrores_cuandoLongitudesYValorSuperanLimites() {
        // Act & Assert
        assertThatThrownBy(() -> ItemCuantitativoJuradoDomain.crear(
                "n".repeat(101), "d".repeat(301), CATEGORIA, 501))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.ItemCuantitativoJurado.NOMBRE_DEMASIADO_LARGO,
                                        EvaluacionesCodes.ItemCuantitativoJurado.DESCRIPCION_DEMASIADO_LARGA,
                                        EvaluacionesCodes.ItemCuantitativoJurado.VALOR_FUERA_DE_RANGO));
    }

    @Test
    void debeAceptarValoresLimite() {
        // Act
        ItemCuantitativoJuradoDomain minimo = ItemCuantitativoJuradoDomain.crear(
                "n".repeat(100), "d".repeat(300), CATEGORIA, 0);
        ItemCuantitativoJuradoDomain maximo = ItemCuantitativoJuradoDomain.crear(
                "Máximo", "Descripción", CATEGORIA, 500);

        // Assert
        assertThat(minimo.getValor()).isZero();
        assertThat(maximo.getValor()).isEqualTo(500);
    }

    @Test
    void debeReconstruirSinRegenerarDatos() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        ItemCuantitativoJuradoDomain item = ItemCuantitativoJuradoDomain.reconstruir(
                id, "Nombre", "Descripción", CATEGORIA, 50);

        // Assert
        assertThat(item.getId()).isEqualTo(id);
        assertThat(item.getNombre()).isEqualTo("Nombre");
        assertThat(item.getDescripcion()).isEqualTo("Descripción");
        assertThat(item.getCategoria()).isEqualTo(CATEGORIA);
        assertThat(item.getValor()).isEqualTo(50);
    }
}
