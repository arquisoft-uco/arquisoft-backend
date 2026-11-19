package com.arquisoft.mapas_ruta.domain.proyectogrado.model;

import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.EstadoProyectoGradoNoEncontradoException;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
    void debeContenerSoloLasConstantesDelMer_cuandoSeListaElCatalogo() {
        // Act
        var nombres = Arrays.stream(EstadoProyectoGrado.values()).map(Enum::name).toList();

        // Assert
        assertThat(nombres).containsExactly("EN_PROCESO", "ATRASADO", "FINALIZADO", "CANCELADO", "VACIO");
    }

    @Test
    void debeAdmitirCambios_cuandoEstaEnProcesoOAtrasado() {
        // Act & Assert
        assertThat(EstadoProyectoGrado.EN_PROCESO.admiteCambios()).isTrue();
        assertThat(EstadoProyectoGrado.ATRASADO.admiteCambios()).isTrue();
    }

    @Test
    void debeNoAdmitirCambios_cuandoEstaFinalizadoCanceladoOVacio() {
        // Act & Assert
        assertThat(EstadoProyectoGrado.FINALIZADO.admiteCambios()).isFalse();
        assertThat(EstadoProyectoGrado.CANCELADO.admiteCambios()).isFalse();
        assertThat(EstadoProyectoGrado.VACIO.admiteCambios()).isFalse();
    }
}
