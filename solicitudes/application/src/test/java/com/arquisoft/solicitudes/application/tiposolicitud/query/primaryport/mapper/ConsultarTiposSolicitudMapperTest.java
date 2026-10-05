package com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.mapper;

import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudQuery;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarTiposSolicitudMapperTest {

    @Test
    void debeConservarLosTipos_cuandoConvierteQueryACriteria() {
        // Arrange
        var query = ConsultarTiposSolicitudQuery.crear(Set.of("CAMBIO_DE_ASESOR", "AMPLIACION_DE_PLAZO"));

        // Act
        var criteria = ConsultarTiposSolicitudMapper.toCriteria(query);

        // Assert
        assertThat(criteria.tipos()).containsExactlyInAnyOrder("CAMBIO_DE_ASESOR", "AMPLIACION_DE_PLAZO");
    }
}
