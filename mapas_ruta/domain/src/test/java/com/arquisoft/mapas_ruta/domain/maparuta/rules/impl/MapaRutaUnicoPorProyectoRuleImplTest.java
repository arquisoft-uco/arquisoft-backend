package com.arquisoft.mapas_ruta.domain.maparuta.rules.impl;

import com.arquisoft.mapas_ruta.domain.maparuta.exception.MapaRutaDuplicadoException;
import com.arquisoft.mapas_ruta.domain.maparuta.model.DisponibilidadMapaRuta;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MapaRutaUnicoPorProyectoRuleImplTest {

    private static final String TITULO_PROYECTO = "Sistema de gestión académica";

    private final MapaRutaUnicoPorProyectoRuleImpl rule = new MapaRutaUnicoPorProyectoRuleImpl();

    @Test
    void debePasar_cuandoElProyectoNoTieneMapaRuta() {
        // Arrange
        var disponibilidad = new DisponibilidadMapaRuta(UtilUUID.generarNuevoUUID(), TITULO_PROYECTO, false);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarDuplicado_cuandoElProyectoYaTieneMapaRuta() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var disponibilidad = new DisponibilidadMapaRuta(proyectoGrado, TITULO_PROYECTO, true);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOfSatisfying(MapaRutaDuplicadoException.class, excepcion -> {
                    assertThat(excepcion.getCodigoError()).isEqualTo(MapasRutaCodes.MapaRuta.MAPA_RUTA_DUPLICADO);
                    assertThat(excepcion.getError().getMessage())
                            .contains(TITULO_PROYECTO)
                            .doesNotContain(proyectoGrado.toString());
                    assertThat(excepcion.getMessage()).contains(proyectoGrado.toString());
                });
    }
}
