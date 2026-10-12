package com.arquisoft.proyectos.domain.estadoasesor;

import com.arquisoft.proyectos.domain.estadoasesor.exception.EstadoAsesorNoEncontradoException;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoAsesorTest {

    @Test
    void debeRetornarElEstado_cuandoElIdCoincideConElCatalogo() {
        // Act
        var estado = EstadoAsesor.desde("FINALIZADO");

        // Assert
        assertThat(estado).isEqualTo(EstadoAsesor.FINALIZADO);
        assertThat(estado.getId()).isEqualTo("FINALIZADO");
        assertThat(estado.getNombre()).isEqualTo("Finalizado");
        assertThat(EstadoAsesor.esValido("ASIGNADO")).isTrue();
    }

    @Test
    void debeRechazarElId_cuandoEsNuloDesconocidoOElCentinela() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoAsesor.desde("VACIO"))
                .isInstanceOf(EstadoAsesorNoEncontradoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.EstadoAsesor.NO_ENCONTRADO);
        assertThatThrownBy(() -> EstadoAsesor.desde("NO_EXISTE"))
                .isInstanceOf(EstadoAsesorNoEncontradoException.class);
        assertThat(EstadoAsesor.esValido(null)).isFalse();
        assertThat(EstadoAsesor.esValido("VACIO")).isFalse();
    }
}
