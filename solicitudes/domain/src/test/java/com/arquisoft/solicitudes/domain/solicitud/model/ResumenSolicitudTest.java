package com.arquisoft.solicitudes.domain.solicitud.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResumenSolicitudTest {

    @Test
    void debeReportarVacio_cuandoEsElCentinela() {
        assertThat(ResumenSolicitud.VACIO.esVacio()).isTrue();
    }

    @Test
    void debeReportarNoVacio_cuandoTieneDatosReales() {
        var resumen = new ResumenSolicitud(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "NOVEDAD_PARA_EL_COORDINADOR");

        assertThat(resumen.esVacio()).isFalse();
    }
}
