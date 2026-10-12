package com.arquisoft.mapas_ruta.domain.maparuta.rules.impl;

import com.arquisoft.mapas_ruta.domain.maparuta.exception.FechaFinMapaRutaNoPosteriorException;
import com.arquisoft.mapas_ruta.domain.maparuta.model.RangoFechasMapaRuta;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FechaFinPosteriorAInicioRuleImplTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 10, 1);

    private final FechaFinPosteriorAInicioRuleImpl rule = new FechaFinPosteriorAInicioRuleImpl();

    @Test
    void debePasar_cuandoFinPosterior() {
        // Arrange
        var rango = new RangoFechasMapaRuta(UtilUUID.generarNuevoUUID(), INICIO, INICIO.plusDays(1));

        // Act & Assert
        assertThatCode(() -> rule.validar(rango)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoFinIgual() {
        // Arrange
        var rango = new RangoFechasMapaRuta(UtilUUID.generarNuevoUUID(), INICIO, INICIO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(rango))
                .isInstanceOf(FechaFinMapaRutaNoPosteriorException.class)
                .hasFieldOrPropertyWithValue("codigoError", MapasRutaCodes.MapaRuta.FECHA_FIN_NO_POSTERIOR);
    }

    @Test
    void debeLanzar_cuandoFinAnterior() {
        // Arrange
        var rango = new RangoFechasMapaRuta(UtilUUID.generarNuevoUUID(), INICIO, INICIO.minusDays(1));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(rango))
                .isInstanceOf(FechaFinMapaRutaNoPosteriorException.class);
    }
}
