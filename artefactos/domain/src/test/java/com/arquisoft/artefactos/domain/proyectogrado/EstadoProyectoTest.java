package com.arquisoft.artefactos.domain.proyectogrado;

import com.arquisoft.artefactos.domain.proyectogrado.exception.EstadoProyectoNoEncontradoException;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyecto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoProyectoTest {

    @Test
    void debeResolverCadaEstadoDelCatalogo_cuandoElIdEsValido() {
        // Act & Assert
        assertThat(EstadoProyecto.desde("EN_PROCESO")).isEqualTo(EstadoProyecto.EN_PROCESO);
        assertThat(EstadoProyecto.desde("ATRASADO")).isEqualTo(EstadoProyecto.ATRASADO);
        assertThat(EstadoProyecto.desde("FINALIZADO")).isEqualTo(EstadoProyecto.FINALIZADO);
        assertThat(EstadoProyecto.desde("CANCELADO")).isEqualTo(EstadoProyecto.CANCELADO);
        assertThat(EstadoProyecto.FINALIZADO.getId()).isEqualTo("FINALIZADO");
        assertThat(EstadoProyecto.FINALIZADO.getNombre()).isEqualTo("Finalizado");
    }

    @Test
    void debeLanzarEstadoNoEncontrado_cuandoElIdNoEstaEnElCatalogo() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoProyecto.desde("LISTO_PARA_REVISION"))
                .isInstanceOf(EstadoProyectoNoEncontradoException.class)
                .satisfies(ex -> assertThat(((EstadoProyectoNoEncontradoException) ex).getCodigoError())
                        .isEqualTo(ArtefactosCodes.ProyectoGrado.ESTADO_PROYECTO_NO_ENCONTRADO));
    }

    @Test
    void debeRechazarVacioYNulos_cuandoSePreguntaPorValidez() {
        // Act & Assert
        assertThat(EstadoProyecto.esValido("CANCELADO")).isTrue();
        assertThat(EstadoProyecto.esValido("VACIO")).isFalse();
        assertThat(EstadoProyecto.esValido("otro")).isFalse();
        assertThat(EstadoProyecto.esValido(null)).isFalse();
    }
}
