package com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.CupoAsesoresExcedidoException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.CupoAsesoresProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosLimits;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorProyectoGradoCupoDisponibleRuleImplTest {

    private final AsesorProyectoGradoCupoDisponibleRuleImpl regla = new AsesorProyectoGradoCupoDisponibleRuleImpl();

    @Test
    void debePasar_cuandoHayUnCupoLibre() {
        // Arrange
        var cupo = new CupoAsesoresProyectoGrado(ProyectosLimits.AsesorProyectoGrado.MAX_ASESORES_ASIGNADOS - 1);

        // Act & Assert
        assertThatCode(() -> regla.validar(cupo)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoSeAlcanzaElMaximo() {
        // Arrange
        var cupo = new CupoAsesoresProyectoGrado(ProyectosLimits.AsesorProyectoGrado.MAX_ASESORES_ASIGNADOS);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(cupo))
                .isInstanceOf(CupoAsesoresExcedidoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.AsesorProyectoGrado.CUPO_ASESORES_EXCEDIDO);
    }
}
