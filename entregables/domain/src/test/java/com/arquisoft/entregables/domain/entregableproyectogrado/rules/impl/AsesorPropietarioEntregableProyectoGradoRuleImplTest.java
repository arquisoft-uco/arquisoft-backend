package com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.AsesorNoPropietarioEntregableProyectoGradoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.PropiedadEntregableProyectoGrado;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorPropietarioEntregableProyectoGradoRuleImplTest {

    private final AsesorPropietarioEntregableProyectoGradoRuleImpl rule =
            new AsesorPropietarioEntregableProyectoGradoRuleImpl();

    @Test
    void debePasar_cuandoElAsesorEsElAsignado() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> rule.validar(
                new PropiedadEntregableProyectoGrado(UtilUUID.generarNuevoUUID(), asesor, asesor)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarAsesorNoPropietario_cuandoElAsesorEsOtro() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var asignado = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new PropiedadEntregableProyectoGrado(entregable, asignado, solicitante)))
                .isInstanceOfSatisfying(AsesorNoPropietarioEntregableProyectoGradoException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(EntregablesCodes.EntregableProyectoGrado.ASESOR_NO_PROPIETARIO);
                    assertThat(ex.getError().getMessage())
                            .doesNotContain(entregable.toString(), solicitante.toString());
                    assertThat(ex.getMessage()).contains(entregable.toString(), solicitante.toString());
                });
    }
}
