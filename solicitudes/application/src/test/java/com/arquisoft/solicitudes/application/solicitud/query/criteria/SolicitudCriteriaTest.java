package com.arquisoft.solicitudes.application.solicitud.query.criteria;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudCriteriaTest {

    @Test
    void debeTenerTipoSolicitudVacio_cuandoNoSeFija() {
        // Arrange & Act
        var criteria = SolicitudCriteria.builder().pagina(0).tamanio(10).build();

        // Assert
        assertThat(criteria.getTipoSolicitud()).isEmpty();
    }

    @Test
    void debeExponerElTipoSolicitud_cuandoSeFijaEnElBuilder() {
        // Arrange & Act
        var criteria = SolicitudCriteria.builder().pagina(0).tamanio(10).tipoSolicitud("CAMBIO_DE_ASESOR").build();

        // Assert
        assertThat(criteria.getTipoSolicitud()).isEqualTo("CAMBIO_DE_ASESOR");
    }
}
