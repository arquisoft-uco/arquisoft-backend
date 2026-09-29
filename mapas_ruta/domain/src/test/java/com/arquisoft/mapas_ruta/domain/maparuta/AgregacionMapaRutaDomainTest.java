package com.arquisoft.mapas_ruta.domain.maparuta;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregacionMapaRutaDomainTest {

    @Test
    void debeComponerLaAgregacion_cuandoMapaYCoordinadorEstanPresentes() {
        // Arrange
        var mapaRuta = MapaRutaDomain.crear(
                UtilUUID.generarNuevoUUID(), LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var agregacion = AgregacionMapaRutaDomain.crear(mapaRuta, coordinador);

        // Assert
        assertThat(agregacion.getMapaRuta()).isSameAs(mapaRuta);
        assertThat(agregacion.getCoordinador()).isEqualTo(coordinador);
    }

    @Test
    void debeAcumularErrores_cuandoMapaYCoordinadorSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> AgregacionMapaRutaDomain.crear(null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        MapasRutaCodes.MapaRuta.MAPA_RUTA_REQUERIDO,
                                        MapasRutaCodes.MapaRuta.COORDINADOR_REQUERIDO));
    }
}
