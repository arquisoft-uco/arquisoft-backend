package com.arquisoft.mapas_ruta.domain.proyectogrado.rules.impl;

import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoPropietarioException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.PropiedadProyectoGrado;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordinadorPropietarioProyectoGradoRuleImplTest {

    private static final String TITULO_PROYECTO = "Sistema de gestión académica";

    private final CoordinadorPropietarioProyectoGradoRuleImpl rule =
            new CoordinadorPropietarioProyectoGradoRuleImpl();

    @Test
    void debePasar_cuandoElSolicitanteEsElCoordinadorAsignado() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var propiedad = new PropiedadProyectoGrado(
                UtilUUID.generarNuevoUUID(), TITULO_PROYECTO, coordinador, coordinador);

        // Act & Assert
        assertThatCode(() -> rule.validar(propiedad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoPropietario_cuandoElSolicitanteEsOtroCoordinador() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var propiedad = new PropiedadProyectoGrado(
                proyectoGrado, TITULO_PROYECTO, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(propiedad))
                .isInstanceOfSatisfying(ProyectoGradoNoPropietarioException.class, excepcion -> {
                    assertThat(excepcion.getCodigoError()).isEqualTo(MapasRutaCodes.ProyectoGrado.NO_PROPIETARIO);
                    assertThat(excepcion.getError().getMessage())
                            .contains(TITULO_PROYECTO)
                            .doesNotContain(proyectoGrado.toString());
                    assertThat(excepcion.getMessage()).contains(proyectoGrado.toString());
                });
    }
}
