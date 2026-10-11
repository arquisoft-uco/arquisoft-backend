package com.arquisoft.proyectos.domain.estadoproyectogrado;

import com.arquisoft.proyectos.domain.estadoproyectogrado.exception.EstadoProyectoGradoNoEncontradoException;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoProyectoGradoTest {

    @Test
    void debeRetornarElEstado_cuandoElIdCoincideConElCatalogo() {
        // Act
        var estado = EstadoProyectoGrado.desde("EN_PROCESO");

        // Assert
        assertThat(estado).isEqualTo(EstadoProyectoGrado.EN_PROCESO);
        assertThat(estado.getId()).isEqualTo("EN_PROCESO");
        assertThat(estado.getNombre()).isEqualTo("En proceso");
        assertThat(EstadoProyectoGrado.esValido("FINALIZADO")).isTrue();
    }

    @Test
    void debeRechazarElId_cuandoEsNuloDesconocidoOElCentinela() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoProyectoGrado.desde("VACIO"))
                .isInstanceOf(EstadoProyectoGradoNoEncontradoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.EstadoProyectoGrado.NO_ENCONTRADO);
        assertThatThrownBy(() -> EstadoProyectoGrado.desde("NO_EXISTE"))
                .isInstanceOf(EstadoProyectoGradoNoEncontradoException.class);
        assertThat(EstadoProyectoGrado.esValido(null)).isFalse();
        assertThat(EstadoProyectoGrado.esValido("VACIO")).isFalse();
    }
}
