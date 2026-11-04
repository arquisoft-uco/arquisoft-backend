package com.arquisoft.mapas_ruta.domain.proyectogrado.rules.impl;

import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoEnProcesoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoEnProcesoRuleImplTest {

    private static final String TITULO_PROYECTO = "Sistema de gestión académica";

    private final ProyectoGradoEnProcesoRuleImpl rule = new ProyectoGradoEnProcesoRuleImpl();

    @Test
    void debePasar_cuandoElProyectoEstaEnProceso() {
        // Arrange
        var estado = new EstadoActualProyectoGrado(
                UtilUUID.generarNuevoUUID(), TITULO_PROYECTO, EstadoProyectoGrado.EN_PROCESO);

        // Act & Assert
        assertThatCode(() -> rule.validar(estado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEnProceso_cuandoElProyectoEstaAtrasado() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estado = new EstadoActualProyectoGrado(proyectoGrado, TITULO_PROYECTO, EstadoProyectoGrado.ATRASADO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(estado))
                .isInstanceOfSatisfying(ProyectoGradoNoEnProcesoException.class, excepcion -> {
                    assertThat(excepcion.getCodigoError()).isEqualTo(MapasRutaCodes.ProyectoGrado.NO_EN_PROCESO);
                    assertThat(excepcion.getError().getMessage())
                            .contains(TITULO_PROYECTO)
                            .contains(EstadoProyectoGrado.ATRASADO.getNombre())
                            .doesNotContain(proyectoGrado.toString());
                    assertThat(excepcion.getMessage()).contains(proyectoGrado.toString());
                });
    }
}
