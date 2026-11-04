package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoPerteneceCoordinadorException;
import com.arquisoft.proyectos.domain.proyectogrado.model.PropiedadCoordinadorProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordinadorProyectoGradoPropietarioRuleImplTest {

    private final CoordinadorProyectoGradoPropietarioRuleImpl regla = new CoordinadorProyectoGradoPropietarioRuleImpl();

    @Test
    void debePasar_cuandoElSolicitanteEsElCoordinadorDelProyecto() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var propiedad = new PropiedadCoordinadorProyectoGrado(UtilUUID.generarNuevoUUID(), coordinador, coordinador);

        // Act & Assert
        assertThatCode(() -> regla.validar(propiedad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoPertenece_cuandoElSolicitanteEsOtroCoordinador() {
        // Arrange
        var propiedad = new PropiedadCoordinadorProyectoGrado(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(propiedad))
                .isInstanceOf(ProyectoGradoNoPerteneceCoordinadorException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.ProyectoGrado.NO_PERTENECE_COORDINADOR);
    }

    @Test
    void debeLanzarNoPertenece_cuandoElProyectoNoTieneCoordinador() {
        // Arrange
        var propiedad = new PropiedadCoordinadorProyectoGrado(
                UtilUUID.generarNuevoUUID(), null, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(propiedad))
                .isInstanceOf(ProyectoGradoNoPerteneceCoordinadorException.class);
    }
}
