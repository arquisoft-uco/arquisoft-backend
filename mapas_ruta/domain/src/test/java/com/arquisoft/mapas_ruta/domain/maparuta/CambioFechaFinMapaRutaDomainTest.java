package com.arquisoft.mapas_ruta.domain.maparuta;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambioFechaFinMapaRutaDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var fechaFin = LocalDate.of(2026, 12, 15);

        // Act
        var cambio = CambioFechaFinMapaRutaDomain.crear(mapaRuta, fechaFin, coordinador);

        // Assert
        assertThat(cambio.getMapaRuta()).isEqualTo(mapaRuta);
        assertThat(cambio.getFechaFin()).isEqualTo(fechaFin);
        assertThat(cambio.getCoordinador()).isEqualTo(coordinador);
    }

    @Test
    void debeAcumularFieldErrors_cuandoTodosNulos() {
        // Act & Assert
        assertThatThrownBy(() -> CambioFechaFinMapaRutaDomain.crear(null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        MapasRutaCodes.MapaRuta.MAPA_RUTA_REQUERIDO,
                                        MapasRutaCodes.MapaRuta.FECHA_FIN_REQUERIDA,
                                        MapasRutaCodes.MapaRuta.COORDINADOR_REQUERIDO));
    }
}
