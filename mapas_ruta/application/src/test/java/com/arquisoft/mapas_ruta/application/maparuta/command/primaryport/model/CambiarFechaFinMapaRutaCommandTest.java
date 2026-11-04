package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarFechaFinMapaRutaCommandTest {

    @Test
    void debeCrear_cuandoValido() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var command = CambiarFechaFinMapaRutaCommand.crear(mapaRuta, coordinador, "2026-12-15");

        // Assert
        assertThat(command.mapaRuta()).isEqualTo(mapaRuta);
        assertThat(command.coordinador()).isEqualTo(coordinador);
        assertThat(command.fechaFin()).isEqualTo(LocalDate.of(2026, 12, 15));
    }

    @Test
    void debeLanzar400_cuandoFechaFinBlanca() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambiarFechaFinMapaRutaCommand.crear(mapaRuta, coordinador, " "))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactly(MapasRutaCodes.MapaRuta.FECHA_FIN_REQUERIDA));
    }

    @Test
    void debeLanzar400_cuandoFechaFinInvalida() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambiarFechaFinMapaRutaCommand.crear(mapaRuta, coordinador, "2026-02-30"))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactly(MapasRutaCodes.MapaRuta.FECHA_FIN_INVALIDA));
    }

    @Test
    void debeAcumularErrores_cuandoMapaYCoordinadorNulos() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarFechaFinMapaRutaCommand.crear(null, null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        MapasRutaCodes.MapaRuta.MAPA_RUTA_REQUERIDO,
                                        MapasRutaCodes.MapaRuta.COORDINADOR_REQUERIDO,
                                        MapasRutaCodes.MapaRuta.FECHA_FIN_REQUERIDA));
    }
}
