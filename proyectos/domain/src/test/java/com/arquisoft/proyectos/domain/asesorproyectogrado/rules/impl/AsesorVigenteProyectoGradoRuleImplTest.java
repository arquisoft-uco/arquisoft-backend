package com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorNoVigenteException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.VigenciaAsesorProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorVigenteProyectoGradoRuleImplTest {

    private final AsesorVigenteProyectoGradoRuleImpl regla = new AsesorVigenteProyectoGradoRuleImpl();

    @Test
    void debePasar_cuandoExisteYNoEstaEliminado() {
        // Arrange
        var vigencia = new VigenciaAsesorProyectoGrado(UtilUUID.generarNuevoUUID(), true, false);

        // Act & Assert
        assertThatCode(() -> regla.validar(vigencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoNoExisteEnLaReplica() {
        // Arrange
        var vigencia = new VigenciaAsesorProyectoGrado(UtilUUID.generarNuevoUUID(), false, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOf(AsesorNoVigenteException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.AsesorProyectoGrado.ASESOR_NO_VIGENTE);
    }

    @Test
    void debeLanzarExcepcion_cuandoEstaDadoDeBaja() {
        // Arrange
        var vigencia = new VigenciaAsesorProyectoGrado(UtilUUID.generarNuevoUUID(), true, true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia)).isInstanceOf(AsesorNoVigenteException.class);
    }
}
