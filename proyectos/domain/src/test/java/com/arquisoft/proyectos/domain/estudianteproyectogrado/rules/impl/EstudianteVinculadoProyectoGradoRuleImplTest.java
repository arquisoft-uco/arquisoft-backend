package com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteNoVinculadoException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ExistenciaVinculoEstudianteProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteVinculadoProyectoGradoRuleImplTest {

    private final EstudianteVinculadoProyectoGradoRuleImpl regla = new EstudianteVinculadoProyectoGradoRuleImpl();

    @Test
    void debePasar_cuandoElEstudianteEstaVinculado() {
        // Arrange
        var existencia = new ExistenciaVinculoEstudianteProyectoGrado(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoVinculado_cuandoElEstudianteNoEstaVinculado() {
        // Arrange
        var existencia = new ExistenciaVinculoEstudianteProyectoGrado(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(EstudianteNoVinculadoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_NO_VINCULADO);
    }
}
