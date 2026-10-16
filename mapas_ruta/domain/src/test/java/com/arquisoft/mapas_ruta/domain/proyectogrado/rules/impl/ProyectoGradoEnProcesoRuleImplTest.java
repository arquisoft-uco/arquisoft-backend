package com.arquisoft.mapas_ruta.domain.proyectogrado.rules.impl;

import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoEnProcesoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoEnProcesoRuleImplTest {

    private final ProyectoGradoEnProcesoRuleImpl rule = new ProyectoGradoEnProcesoRuleImpl();

    @Test
    void debePasar_cuandoElProyectoEstaEnProceso() {
        // Arrange
        var estado = new EstadoActualProyectoGrado(UtilUUID.generarNuevoUUID(), EstadoProyectoGrado.EN_PROCESO);

        // Act & Assert
        assertThatCode(() -> rule.validar(estado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEnProceso_cuandoElProyectoEstaAtrasado() {
        // Arrange
        var estado = new EstadoActualProyectoGrado(UtilUUID.generarNuevoUUID(), EstadoProyectoGrado.ATRASADO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(estado))
                .isInstanceOf(ProyectoGradoNoEnProcesoException.class)
                .hasFieldOrPropertyWithValue("codigoError", MapasRutaCodes.ProyectoGrado.NO_EN_PROCESO);
    }
}
