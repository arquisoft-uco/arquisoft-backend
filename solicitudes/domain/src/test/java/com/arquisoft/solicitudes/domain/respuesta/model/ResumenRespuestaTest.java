package com.arquisoft.solicitudes.domain.respuesta.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResumenRespuestaTest {

    @Test
    void debeReportarVacio_cuandoEsElCentinela() {
        // Act
        var esVacio = ResumenRespuesta.VACIO.esVacio();

        // Assert
        assertThat(esVacio).isTrue();
    }

    @Test
    void debeReportarNoVacio_cuandoTieneDatosReales() {
        // Arrange
        var resumen = new ResumenRespuesta(UUID.randomUUID(), "EN_REVISION");

        // Act
        var esVacio = resumen.esVacio();

        // Assert
        assertThat(esVacio).isFalse();
    }
}
