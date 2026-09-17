package com.arquisoft.solicitudes.domain.respuesta.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResumenRespuestaTest {

    @Test
    void debeReportarVacio_cuandoEsElCentinela() {
        assertThat(ResumenRespuesta.VACIO.esVacio()).isTrue();
    }

    @Test
    void debeReportarNoVacio_cuandoTieneDatosReales() {
        var resumen = new ResumenRespuesta(UUID.randomUUID(), "EN_REVISION");

        assertThat(resumen.esVacio()).isFalse();
    }
}
