package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.primaryadapter.web.mapper;

import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarTiposSolicitudRequestMapperTest {

    @Test
    void debeConservarLosIds_cuandoConstruyeElQuery() {
        // Act
        var query = ConsultarTiposSolicitudRequestMapper.toQuery(Set.of("CAMBIO_DE_ASESOR", "AMPLIACION_DE_PLAZO"));

        // Assert
        assertThat(query.tipos()).containsExactlyInAnyOrder("CAMBIO_DE_ASESOR", "AMPLIACION_DE_PLAZO");
    }

    @Test
    void debeAceptarConjuntoVacio_cuandoNoHayTiposPermitidos() {
        // Act
        var query = ConsultarTiposSolicitudRequestMapper.toQuery(Set.of());

        // Assert
        assertThat(query.tipos()).isEmpty();
    }

    @Test
    void debePropagarApplicationValidationException_cuandoConjuntoEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarTiposSolicitudRequestMapper.toQuery(null))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
