package com.arquisoft.mapas_ruta.domain.planestimado.rules.impl;

import com.arquisoft.mapas_ruta.domain.planestimado.exception.TiempoPlanesExcedePlazoException;
import com.arquisoft.mapas_ruta.domain.planestimado.model.PlazoPlanesEstimados;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TiempoPlanesDentroDelPlazoRuleImplTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 10, 1);
    private static final LocalDate FIN = LocalDate.of(2026, 10, 11);

    private final TiempoPlanesDentroDelPlazoRuleImpl rule = new TiempoPlanesDentroDelPlazoRuleImpl();

    @Test
    void noDebeLanzar_cuandoAcumuladoConNuevoEsMenorAlPlazo() {
        // Arrange
        var plazo = new PlazoPlanesEstimados(UtilUUID.generarNuevoUUID(), INICIO, FIN, 3, 4);

        // Act & Assert
        assertThatCode(() -> rule.validar(plazo)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoAcumuladoConNuevoIgualaElPlazo() {
        // Arrange
        var plazo = new PlazoPlanesEstimados(UtilUUID.generarNuevoUUID(), INICIO, FIN, 4, 6);

        // Act & Assert
        assertThatCode(() -> rule.validar(plazo)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoAcumuladoConNuevoExcedeElPlazo() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var plazo = new PlazoPlanesEstimados(mapaRuta, INICIO, FIN, 8, 3);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(plazo))
                .isInstanceOfSatisfying(TiempoPlanesExcedePlazoException.class, excepcion -> {
                    assertThat(excepcion.getCodigoError())
                            .isEqualTo(MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TIEMPO_EXCEDE_PLAZO);
                    assertThat(excepcion.getError().getMessage()).doesNotContain(mapaRuta.toString());
                    assertThat(excepcion.getMessage()).contains(mapaRuta.toString());
                });
    }

    @Test
    void debeLanzar_cuandoSoloElNuevoPlanExcedeElPlazo() {
        // Arrange
        var plazo = new PlazoPlanesEstimados(UtilUUID.generarNuevoUUID(), INICIO, FIN, 0, 11);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(plazo)).isInstanceOf(TiempoPlanesExcedePlazoException.class);
    }
}
