package com.arquisoft.proyectos.domain.estadoentregable;

import com.arquisoft.proyectos.domain.estadoentregable.exception.EstadoEntregableNoEncontradoException;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoEntregableTest {

    @Test
    void debeRetornarElEstado_cuandoElIdCoincideConElCatalogo() {
        // Act
        var estado = EstadoEntregable.desde("DISPONIBLE_EVALUACION");

        // Assert
        assertThat(estado).isEqualTo(EstadoEntregable.DISPONIBLE_EVALUACION);
        assertThat(estado.getId()).isEqualTo("DISPONIBLE_EVALUACION");
        assertThat(estado.getNombre()).isEqualTo("Disponible Evaluacion");
        assertThat(EstadoEntregable.esValido("CERRADO")).isTrue();
        assertThat(EstadoEntregable.esValido("CANCELADO")).isTrue();
    }

    @Test
    void debeRechazarElId_cuandoEsNuloDesconocidoOElCentinela() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoEntregable.desde("VACIO"))
                .isInstanceOf(EstadoEntregableNoEncontradoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.EstadoEntregable.NO_ENCONTRADO);
        assertThatThrownBy(() -> EstadoEntregable.desde("NO_EXISTE"))
                .isInstanceOf(EstadoEntregableNoEncontradoException.class);
        assertThat(EstadoEntregable.esValido(null)).isFalse();
        assertThat(EstadoEntregable.esValido("VACIO")).isFalse();
    }
}
