package com.arquisoft.solicitudes.domain.tiposolicitud;

import com.arquisoft.solicitudes.domain.tiposolicitud.exception.TipoSolicitudNoEncontradoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TipoSolicitudTest {

    @Test
    void debeRetornarLaConstante_cuandoElIdCoincideConElCatalogo() {
        assertThat(TipoSolicitud.desde("NOVEDAD_PARA_EL_COORDINADOR"))
                .isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
    }

    @Test
    void debeLanzar_cuandoElIdEsDesconocidoNuloEnBlancoOElCentinela() {
        assertThatThrownBy(() -> TipoSolicitud.desde("INEXISTENTE"))
                .isInstanceOf(TipoSolicitudNoEncontradoException.class);
        assertThatThrownBy(() -> TipoSolicitud.desde(null))
                .isInstanceOf(TipoSolicitudNoEncontradoException.class);
        assertThatThrownBy(() -> TipoSolicitud.desde("  "))
                .isInstanceOf(TipoSolicitudNoEncontradoException.class);
        assertThatThrownBy(() -> TipoSolicitud.desde("VACIO"))
                .isInstanceOf(TipoSolicitudNoEncontradoException.class);
    }

    @Test
    void debeReportarValidez_sinLanzar_cuandoSeConsultaConEsValido() {
        assertThat(TipoSolicitud.esValido("CAMBIO_DE_ASESOR")).isTrue();
        assertThat(TipoSolicitud.esValido("NO_EXISTE")).isFalse();
        assertThat(TipoSolicitud.esValido("VACIO")).isFalse();
        assertThat(TipoSolicitud.esValido(null)).isFalse();
    }

    @Test
    void debeExponerElIdIgualAlNombreDeLaConstante() {
        assertThat(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId())
                .isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.name());
    }

    @Test
    void debeExponerElNombreLegibleDelCatalogo() {
        assertThat(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getNombre())
                .isEqualTo("Novedad para el Coordinador");
        assertThat(TipoSolicitud.CAMBIO_DE_ASESOR.getNombre()).isEqualTo("Cambio de Asesor");
        assertThat(TipoSolicitud.VACIO.getNombre()).isEmpty();
    }
}
