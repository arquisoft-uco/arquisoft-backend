package com.arquisoft.mapas_ruta.domain.proyectogrado.model;

import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.EstadoProyectoGradoNoEncontradoException;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoProyectoGradoTest {

    @Test
    void debeResolverElEstado_cuandoElIdEsDelCatalogo() {
        // Act & Assert
        assertThat(EstadoProyectoGrado.desde("EN_PROCESO")).isEqualTo(EstadoProyectoGrado.EN_PROCESO);
        assertThat(EstadoProyectoGrado.desde("ATRASADO").getNombre()).isEqualTo("Atrasado");
        assertThat(EstadoProyectoGrado.desde("ATRASADO").getId()).isEqualTo("ATRASADO");
    }

    @Test
    void debeLanzarNoEncontrado_cuandoElIdNoEsDelCatalogoOEsElCentinela() {
        // Act & Assert
        assertThatThrownBy(() -> EstadoProyectoGrado.desde("INEXISTENTE"))
                .isInstanceOf(EstadoProyectoGradoNoEncontradoException.class)
                .hasFieldOrPropertyWithValue("codigoError", MapasRutaCodes.ProyectoGrado.ESTADO_NO_ENCONTRADO);
        assertThatThrownBy(() -> EstadoProyectoGrado.desde("VACIO"))
                .isInstanceOf(EstadoProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeValidarElId_cuandoSeConsultaEsValido() {
        // Act & Assert
        assertThat(EstadoProyectoGrado.esValido("FINALIZADO")).isTrue();
        assertThat(EstadoProyectoGrado.esValido("INEXISTENTE")).isFalse();
        assertThat(EstadoProyectoGrado.esValido("VACIO")).isFalse();
    }

    @Test
    void debeIndicarEnProceso_soloParaEnProceso() {
        // Act & Assert
        assertThat(EstadoProyectoGrado.EN_PROCESO.estaEnProceso()).isTrue();
        assertThat(EstadoProyectoGrado.ATRASADO.estaEnProceso()).isFalse();
        assertThat(EstadoProyectoGrado.LISTO_PARA_REVISION.estaEnProceso()).isFalse();
    }
}
