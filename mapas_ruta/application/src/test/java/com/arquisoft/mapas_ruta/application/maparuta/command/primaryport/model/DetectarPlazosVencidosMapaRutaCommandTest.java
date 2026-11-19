package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DetectarPlazosVencidosMapaRutaCommandTest {

    @Test
    void debeCrear_cuandoHoyPresente() {
        // Arrange
        var hoy = LocalDate.of(2026, 11, 19);

        // Act
        var command = DetectarPlazosVencidosMapaRutaCommand.crear(hoy);

        // Assert
        assertThat(command.hoy()).isEqualTo(hoy);
    }

    @Test
    void debeLanzar400_cuandoHoyNula() {
        // Act & Assert
        assertThatThrownBy(() -> DetectarPlazosVencidosMapaRutaCommand.crear(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactly(MapasRutaCodes.MapaRuta.HOY_REQUERIDA));
    }
}
