package com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarTiposSolicitudQueryTest {

    @Test
    void debeConservarLosIds_cuandoConjuntoValido() {
        // Arrange
        var tipos = Set.of("CAMBIO_DE_ASESOR", "AMPLIACION_DE_PLAZO");

        // Act
        var query = ConsultarTiposSolicitudQuery.crear(tipos);

        // Assert
        assertThat(query.tipos()).containsExactlyInAnyOrderElementsOf(tipos);
    }

    @Test
    void debeAceptarConjuntoVacio_cuandoElUsuarioNoTienePermisos() {
        // Act
        var query = ConsultarTiposSolicitudQuery.crear(Set.of());

        // Assert
        assertThat(query.tipos()).isEmpty();
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoConjuntoEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarTiposSolicitudQuery.crear(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores()).singleElement().satisfies(error -> {
                            assertThat(error.campo()).isEqualTo(SolicitudesFields.TipoSolicitud.TIPOS);
                            assertThat(error.codigoError()).isEqualTo(SolicitudesCodes.TipoSolicitud.TIPOS_REQUERIDOS);
                        }));
    }

    @Test
    void debeHacerCopiaDefensiva_cuandoElConjuntoOriginalCambia() {
        // Arrange
        var original = new HashSet<>(Set.of("CAMBIO_DE_ASESOR"));
        var query = ConsultarTiposSolicitudQuery.crear(original);

        // Act
        original.add("AMPLIACION_DE_PLAZO");

        // Assert
        assertThat(query.tipos()).containsExactly("CAMBIO_DE_ASESOR");
        assertThatThrownBy(() -> query.tipos().add("OTRO"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
