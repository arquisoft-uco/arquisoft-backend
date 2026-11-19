package com.arquisoft.mapas_ruta.domain.maparuta;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeteccionPlazosVencidosMapaRutaDomainTest {

    @Test
    void debeCrear_cuandoHoyPresente() {
        // Arrange
        var hoy = LocalDate.of(2026, 11, 19);

        // Act
        var deteccion = DeteccionPlazosVencidosMapaRutaDomain.crear(hoy);

        // Assert
        assertThat(deteccion.getHoy()).isEqualTo(hoy);
    }

    @Test
    void debeLanzarValidacion_cuandoHoyNula() {
        // Act & Assert
        assertThatThrownBy(() -> DeteccionPlazosVencidosMapaRutaDomain.crear(null))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactly(MapasRutaCodes.MapaRuta.HOY_REQUERIDA));
    }
}
