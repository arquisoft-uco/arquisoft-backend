package com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorNoVigenteException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.VigenciaAsesorProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorVigenteProyectoGradoRuleImplTest {

    private static final String NOMBRE_ASESOR = "Carlos Ríos";

    private final AsesorVigenteProyectoGradoRuleImpl regla = new AsesorVigenteProyectoGradoRuleImpl();

    @Test
    void debePasar_cuandoNoEstaEliminado() {
        // Arrange
        var vigencia = new VigenciaAsesorProyectoGrado(UtilUUID.generarNuevoUUID(), NOMBRE_ASESOR, false);

        // Act & Assert
        assertThatCode(() -> regla.validar(vigencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcionConElNombreDelAsesor_cuandoEstaDadoDeBaja() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var vigencia = new VigenciaAsesorProyectoGrado(asesor, NOMBRE_ASESOR, true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOfSatisfying(AsesorNoVigenteException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(ProyectosCodes.AsesorProyectoGrado.ASESOR_NO_VIGENTE);
                    assertThat(ex.getError().getMessage())
                            .contains(NOMBRE_ASESOR)
                            .doesNotContain(asesor.toString());
                });
    }
}
