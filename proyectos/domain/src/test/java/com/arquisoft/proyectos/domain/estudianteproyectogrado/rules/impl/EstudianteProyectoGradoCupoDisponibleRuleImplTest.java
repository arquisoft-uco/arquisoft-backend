package com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.CupoEstudiantesExcedidoException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.CupoEstudiantesProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosLimits;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteProyectoGradoCupoDisponibleRuleImplTest {

    private final EstudianteProyectoGradoCupoDisponibleRuleImpl regla = new EstudianteProyectoGradoCupoDisponibleRuleImpl();

    @Test
    void debePasar_cuandoLosVinculadosMasLosNuevosLleganJustoAlMaximo() {
        // Arrange
        var cupo = new CupoEstudiantesProyectoGrado(1, ProyectosLimits.EstudianteProyectoGrado.MAX_ESTUDIANTES - 1);

        // Act & Assert
        assertThatCode(() -> regla.validar(cupo)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoLosVinculadosMasLosNuevosSuperanElMaximo() {
        // Arrange
        var cupo = new CupoEstudiantesProyectoGrado(1, ProyectosLimits.EstudianteProyectoGrado.MAX_ESTUDIANTES);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(cupo))
                .isInstanceOf(CupoEstudiantesExcedidoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.EstudianteProyectoGrado.CUPO_EXCEDIDO);
    }
}
