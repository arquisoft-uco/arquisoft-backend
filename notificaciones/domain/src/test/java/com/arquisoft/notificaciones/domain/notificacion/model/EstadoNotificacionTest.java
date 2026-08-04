package com.arquisoft.notificaciones.domain.notificacion.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoNotificacionTest {

    @Test
    void debeConsiderarTerminalesEnviadaYFallida_cuandoSeConsultaEsTerminal() {
        assertThat(EstadoNotificacion.ENVIADA.esTerminal()).isTrue();
        assertThat(EstadoNotificacion.FALLIDA.esTerminal()).isTrue();
    }

    @Test
    void noDebeConsiderarTerminalPendiente_cuandoSeConsultaEsTerminal() {
        assertThat(EstadoNotificacion.PENDIENTE.esTerminal()).isFalse();
    }

    @Test
    void debeExponerElCodigoDelTipo_cuandoSeConsultaGetCodigo() {
        assertThat(TipoNotificacion.ASESOR_FICHA_CAMBIADO.getCodigo())
                .isEqualTo("ASESOR_FICHA_CAMBIADO");
    }
}
