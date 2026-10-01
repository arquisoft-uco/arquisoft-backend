package com.arquisoft.notificaciones.domain.notificacion.model;

import com.arquisoft.notificaciones.domain.notificacion.exception.EstadoNotificacionNoEncontradoException;
import com.arquisoft.notificaciones.domain.notificacion.exception.TipoNotificacionNoEncontradoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CatalogosNotificacionTest {

    @Test
    void debeRetornarElTipo_cuandoElIdCoincideConElCatalogo() {
        assertThat(TipoNotificacion.desde("ASESOR_FICHA_CAMBIADO"))
                .isEqualTo(TipoNotificacion.ASESOR_FICHA_CAMBIADO);
    }

    @Test
    void debeLanzarExcepcion_cuandoElTipoEsNuloEnBlancoODesconocido() {
        assertThatThrownBy(() -> TipoNotificacion.desde(null))
                .isInstanceOf(TipoNotificacionNoEncontradoException.class);
        assertThatThrownBy(() -> TipoNotificacion.desde(""))
                .isInstanceOf(TipoNotificacionNoEncontradoException.class);
        assertThatThrownBy(() -> TipoNotificacion.desde("NO_EXISTE"))
                .isInstanceOf(TipoNotificacionNoEncontradoException.class);
    }

    @Test
    void debeRetornarElEstado_cuandoElIdCoincideConElCatalogo() {
        assertThat(EstadoNotificacion.desde("ENVIADA")).isEqualTo(EstadoNotificacion.ENVIADA);
    }

    @Test
    void debeLanzarExcepcion_cuandoElEstadoEsNuloEnBlancoODesconocido() {
        assertThatThrownBy(() -> EstadoNotificacion.desde(null))
                .isInstanceOf(EstadoNotificacionNoEncontradoException.class);
        assertThatThrownBy(() -> EstadoNotificacion.desde(""))
                .isInstanceOf(EstadoNotificacionNoEncontradoException.class);
        assertThatThrownBy(() -> EstadoNotificacion.desde("NO_EXISTE"))
                .isInstanceOf(EstadoNotificacionNoEncontradoException.class);
    }

    @Test
    void debeExponerElIdComoNombreDeLaConstante_igualQueLosDemasCatalogos() {
        assertThat(EstadoNotificacion.ENVIADA.getId()).isEqualTo("ENVIADA");
    }

    @Test
    void debeReportarValidez_sinLanzar_cuandoSeConsultaConEsValido() {
        assertThat(TipoNotificacion.esValido("ASESOR_FICHA_CAMBIADO")).isTrue();
        assertThat(TipoNotificacion.esValido("NO_EXISTE")).isFalse();
        assertThat(EstadoNotificacion.esValido("FALLIDA")).isTrue();
        assertThat(EstadoNotificacion.esValido(null)).isFalse();
    }
}
