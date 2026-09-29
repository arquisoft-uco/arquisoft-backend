package com.arquisoft.solicitudes.domain.estadorespuesta;

import com.arquisoft.solicitudes.domain.estadorespuesta.exception.EstadoRespuestaNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoRespuestaTest {

    @Test
    void debeResolverLasTresConstantesDelCatalogo_cuandoSeConsultaConDesde() {
        assertThat(EstadoRespuesta.desde("APROBADA")).isEqualTo(EstadoRespuesta.APROBADA);
        assertThat(EstadoRespuesta.desde("NO_APROBADA")).isEqualTo(EstadoRespuesta.NO_APROBADA);
        assertThat(EstadoRespuesta.desde("EN_REVISION")).isEqualTo(EstadoRespuesta.EN_REVISION);
    }

    @Test
    void debeLanzar_cuandoElIdEsDesconocidoNuloOElCentinela() {
        assertThatThrownBy(() -> EstadoRespuesta.desde("INEXISTENTE"))
                .isInstanceOf(EstadoRespuestaNoEncontradoException.class);
        assertThatThrownBy(() -> EstadoRespuesta.desde(null))
                .isInstanceOf(EstadoRespuestaNoEncontradoException.class);
        assertThatThrownBy(() -> EstadoRespuesta.desde("VACIO"))
                .isInstanceOf(EstadoRespuestaNoEncontradoException.class);
    }

    @Test
    void debeReportarValidezSinLanzar_cuandoSeConsultaConEsValido() {
        assertThat(EstadoRespuesta.esValido("EN_REVISION")).isTrue();
        assertThat(EstadoRespuesta.esValido("NO_EXISTE")).isFalse();
        assertThat(EstadoRespuesta.esValido("VACIO")).isFalse();
        assertThat(EstadoRespuesta.esValido(null)).isFalse();
    }

    @Test
    void debeExponerIdYNombreLegibleDeCadaConstante() {
        assertThat(EstadoRespuesta.APROBADA.getId()).isEqualTo("APROBADA");
        assertThat(EstadoRespuesta.APROBADA.getNombre()).isEqualTo("Aprobada");
        assertThat(EstadoRespuesta.NO_APROBADA.getNombre()).isEqualTo("No aprobada");
        assertThat(EstadoRespuesta.EN_REVISION.getNombre()).isEqualTo("En revisión");
        assertThat(EstadoRespuesta.EN_REVISION.getId())
                .isEqualTo(EstadoRespuesta.EN_REVISION.name());
    }

    @Test
    void debeDeclararSoloLasFilasDelCatalogoMer_masElCentinela() {
        Set<String> constantes = Arrays.stream(EstadoRespuesta.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        assertThat(constantes)
                .containsExactlyInAnyOrder("APROBADA", "NO_APROBADA", "EN_REVISION", "VACIO");
    }
}
