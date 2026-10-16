package com.arquisoft.shared.validation;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatorFechaTest {

    private static final String CAMPO = "fecha";
    private static final String CODIGO = "CODIGO_ERROR";

    @Test
    void debePasar_cuandoLaFechaEsValida() {
        // Arrange
        var result = new ValidationResult();

        // Act
        var valido = ValidatorFecha.fechaValida("2026-10-01", CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isTrue();
        assertThat(result.tieneErrores()).isFalse();
    }

    @Test
    void debeAcumularError_cuandoLaFechaEsBlancaTieneFormatoErroneoOEsImposible() {
        // Arrange
        var result = new ValidationResult();

        // Act
        var enBlanco = ValidatorFecha.fechaValida(" ", CAMPO, CODIGO, result);
        var formato = ValidatorFecha.fechaValida("01/10/2026", CAMPO, CODIGO, result);
        var imposible = ValidatorFecha.fechaValida("2026-02-30", CAMPO, CODIGO, result);

        // Assert
        assertThat(enBlanco).isFalse();
        assertThat(formato).isFalse();
        assertThat(imposible).isFalse();
        assertThat(result.getErrores()).hasSize(3);
        assertThat(result.getErrores().get(0).campo()).isEqualTo(CAMPO);
        assertThat(result.getErrores().get(0).codigoError()).isEqualTo(CODIGO);
        assertThat(result.getErrores().get(0).mensaje())
                .isEqualTo(Mensajes.formatear(ValidadorKey.FECHA_INVALIDA, CAMPO));
    }

    @Test
    void debePasar_cuandoLaFechaEsPosteriorALaReferencia() {
        // Arrange
        var result = new ValidationResult();

        // Act
        var valido = ValidatorFecha.posterior(
                LocalDate.of(2026, 10, 2), LocalDate.of(2026, 10, 1), CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isTrue();
        assertThat(result.tieneErrores()).isFalse();
    }

    @Test
    void debeAcumularError_cuandoLaFechaEsIgualOAnteriorALaReferencia() {
        // Arrange
        var result = new ValidationResult();
        var referencia = LocalDate.of(2026, 10, 1);

        // Act
        var igual = ValidatorFecha.posterior(referencia, referencia, CAMPO, CODIGO, result);
        var anterior = ValidatorFecha.posterior(referencia.minusDays(1), referencia, CAMPO, CODIGO, result);

        // Assert
        assertThat(igual).isFalse();
        assertThat(anterior).isFalse();
        assertThat(result.getErrores()).hasSize(2);
        assertThat(result.getErrores().get(0).mensaje())
                .isEqualTo(Mensajes.formatear(ValidadorKey.FECHA_POSTERIOR, CAMPO, referencia));
    }

    @Test
    void debePasarSinError_cuandoAlgunaDeLasFechasEsNula() {
        // Arrange
        var result = new ValidationResult();

        // Act
        var sinFecha = ValidatorFecha.posterior(null, LocalDate.of(2026, 10, 1), CAMPO, CODIGO, result);
        var sinReferencia = ValidatorFecha.posterior(LocalDate.of(2026, 10, 1), null, CAMPO, CODIGO, result);

        // Assert
        assertThat(sinFecha).isTrue();
        assertThat(sinReferencia).isTrue();
        assertThat(result.tieneErrores()).isFalse();
    }
}
