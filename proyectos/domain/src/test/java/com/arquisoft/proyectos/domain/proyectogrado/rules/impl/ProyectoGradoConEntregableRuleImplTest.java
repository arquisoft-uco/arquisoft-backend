package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoSinEntregableException;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaUltimoEntregable;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoConEntregableRuleImplTest {

    private static final String TITULO_PROYECTO = "Plataforma de seguimiento de proyectos de grado";

    private final ProyectoGradoConEntregableRuleImpl regla = new ProyectoGradoConEntregableRuleImpl();

    @Test
    void debePasar_cuandoElProyectoTieneEntregable() {
        // Arrange
        var existencia = new ExistenciaUltimoEntregable(UtilUUID.generarNuevoUUID(), TITULO_PROYECTO, true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSinEntregable_cuandoElProyectoNoTieneEntregable() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaUltimoEntregable(proyectoGrado, TITULO_PROYECTO, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOfSatisfying(ProyectoGradoSinEntregableException.class, exception -> {
                    assertThat(exception.getCodigoError()).isEqualTo(ProyectosCodes.ProyectoGrado.SIN_ENTREGABLE);
                    assertThat(exception.getError().getMessage())
                            .contains(TITULO_PROYECTO)
                            .doesNotContain(proyectoGrado.toString());
                    assertThat(exception.getMessage()).contains(proyectoGrado.toString());
                });
    }
}
