package com.arquisoft.proyectos.domain.estadoproyecto;

import com.arquisoft.proyectos.domain.estadoproyecto.exception.EstadoProyectoNoEncontradoException;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoProyectoTest {

    @Test
    void debeRetornarElEstado_cuandoElIdCoincideConElCatalogo() {
        // Act
        var estado = EstadoProyecto.desde("EN_PROCESO");

        // Assert
        assertThat(estado).isEqualTo(EstadoProyecto.EN_PROCESO);
        assertThat(estado.getId()).isEqualTo("EN_PROCESO");
        assertThat(estado.getNombre()).isEqualTo("En proceso");
        assertThat(EstadoProyecto.esValido("FINALIZADO")).isTrue();
    }

    @Test
    void debeRechazarElId_cuandoEsNuloDesconocidoOElCentinela() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoProyecto.desde("VACIO"))
                .isInstanceOf(EstadoProyectoNoEncontradoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.EstadoProyecto.NO_ENCONTRADO);
        assertThatThrownBy(() -> EstadoProyecto.desde("NO_EXISTE"))
                .isInstanceOf(EstadoProyectoNoEncontradoException.class);
        assertThat(EstadoProyecto.esValido(null)).isFalse();
        assertThat(EstadoProyecto.esValido("VACIO")).isFalse();
    }
}
