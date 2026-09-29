package com.arquisoft.mapas_ruta.domain.proyectogrado.rules.impl;

import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoPropietarioException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.PropiedadProyectoGrado;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordinadorPropietarioProyectoGradoRuleImplTest {

    private final CoordinadorPropietarioProyectoGradoRuleImpl rule =
            new CoordinadorPropietarioProyectoGradoRuleImpl();

    @Test
    void debePasar_cuandoElSolicitanteEsElCoordinadorAsignado() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var propiedad = new PropiedadProyectoGrado(UtilUUID.generarNuevoUUID(), coordinador, coordinador);

        // Act & Assert
        assertThatCode(() -> rule.validar(propiedad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoPropietario_cuandoElSolicitanteEsOtroCoordinador() {
        // Arrange
        var propiedad = new PropiedadProyectoGrado(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(propiedad))
                .isInstanceOf(ProyectoGradoNoPropietarioException.class)
                .hasFieldOrPropertyWithValue("codigoError", MapasRutaCodes.ProyectoGrado.NO_PROPIETARIO);
    }
}
