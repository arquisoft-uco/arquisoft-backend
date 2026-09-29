package com.arquisoft.mapas_ruta.domain.proyectogrado.rules.impl;

import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoExisteRuleImplTest {

    private final ProyectoGradoExisteRuleImpl rule = new ProyectoGradoExisteRuleImpl();

    @Test
    void debePasar_cuandoElProyectoExiste() {
        // Arrange
        var existencia = new ExistenciaProyectoGrado(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoElProyectoNoExiste() {
        // Arrange
        var existencia = new ExistenciaProyectoGrado(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class)
                .hasFieldOrPropertyWithValue("codigoError", MapasRutaCodes.ProyectoGrado.NO_ENCONTRADO);
    }
}
