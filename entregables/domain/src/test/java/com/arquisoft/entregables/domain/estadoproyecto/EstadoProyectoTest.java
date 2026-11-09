package com.arquisoft.entregables.domain.estadoproyecto;

import com.arquisoft.entregables.domain.estadoproyecto.exception.EstadoProyectoNoEncontradoException;
import com.arquisoft.shared.message.constant.EntregablesCodes;
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
        assertThat(EstadoProyecto.esValido("ATRASADO")).isTrue();
    }

    @Test
    void debeRechazarElId_cuandoEsNuloDesconocidoOElCentinela() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoProyecto.desde("VACIO"))
                .isInstanceOf(EstadoProyectoNoEncontradoException.class)
                .extracting("codigoError")
                .isEqualTo(EntregablesCodes.ProyectoGrado.ESTADO_PROYECTO_NO_ENCONTRADO);
        assertThatThrownBy(() -> EstadoProyecto.desde("LISTO_PARA_REVISION"))
                .isInstanceOf(EstadoProyectoNoEncontradoException.class);
        assertThat(EstadoProyecto.esValido(null)).isFalse();
        assertThat(EstadoProyecto.esValido("VACIO")).isFalse();
    }
}
