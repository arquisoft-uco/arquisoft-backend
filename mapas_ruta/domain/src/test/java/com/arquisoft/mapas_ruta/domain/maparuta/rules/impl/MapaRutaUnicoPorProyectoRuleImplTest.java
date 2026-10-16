package com.arquisoft.mapas_ruta.domain.maparuta.rules.impl;

import com.arquisoft.mapas_ruta.domain.maparuta.exception.MapaRutaDuplicadoException;
import com.arquisoft.mapas_ruta.domain.maparuta.model.DisponibilidadMapaRuta;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MapaRutaUnicoPorProyectoRuleImplTest {

    private final MapaRutaUnicoPorProyectoRuleImpl rule = new MapaRutaUnicoPorProyectoRuleImpl();

    @Test
    void debePasar_cuandoElProyectoNoTieneMapaRuta() {
        // Arrange
        var disponibilidad = new DisponibilidadMapaRuta(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarDuplicado_cuandoElProyectoYaTieneMapaRuta() {
        // Arrange
        var disponibilidad = new DisponibilidadMapaRuta(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOf(MapaRutaDuplicadoException.class)
                .hasFieldOrPropertyWithValue("codigoError", MapasRutaCodes.MapaRuta.MAPA_RUTA_DUPLICADO);
    }
}
