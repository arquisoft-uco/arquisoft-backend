package com.arquisoft.mapas_ruta.domain.maparuta.rules.impl;

import com.arquisoft.mapas_ruta.domain.maparuta.exception.MapaRutaNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.maparuta.model.ExistenciaMapaRuta;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MapaRutaExisteRuleImplTest {

    private final MapaRutaExisteRuleImpl rule = new MapaRutaExisteRuleImpl();

    @Test
    void debePasar_cuandoExiste() {
        // Arrange
        var existencia = new ExistenciaMapaRuta(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoNoExiste() {
        // Arrange
        var existencia = new ExistenciaMapaRuta(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(MapaRutaNoEncontradoException.class)
                .hasFieldOrPropertyWithValue("codigoError", MapasRutaCodes.MapaRuta.MAPA_RUTA_NO_ENCONTRADO);
    }
}
