package com.arquisoft.artefactos.domain.observacion;

import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AvanceObservacionesRevisionDomainTest {

    @Test
    void debeFijarNuevaYEnProgreso_cuandoSeCrea() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();

        // Act
        var avance = AvanceObservacionesRevisionDomain.crear(revision);

        // Assert
        assertThat(avance.getRevisionAsesor()).isEqualTo(revision);
        assertThat(avance.getEstadoOrigen()).isEqualTo(EstadoObservacion.NUEVA);
        assertThat(avance.getEstadoDestino()).isEqualTo(EstadoObservacion.EN_PROGRESO);
    }

    @Test
    void debeLanzarValidacion_cuandoRevisionAsesorEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> AvanceObservacionesRevisionDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).singleElement().satisfies(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.Observacion.REVISION_ASESOR);
                        assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.Observacion.REVISION_ASESOR_ID_REQUERIDO);
                    });
                });
    }
}
