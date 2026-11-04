package com.arquisoft.artefactos.domain.observacion;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminacionObservacionDomainTest {

    @Test
    void debeCrear_cuandoObservacionYAsesorPresentes() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var eliminacion = EliminacionObservacionDomain.crear(observacion, asesor);

        // Assert
        assertThat(eliminacion.getObservacion()).isEqualTo(observacion);
        assertThat(eliminacion.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoObservacionYAsesorNulos() {
        // Act & Assert
        assertThatThrownBy(() -> EliminacionObservacionDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError")
                            .containsExactlyInAnyOrder(ArtefactosCodes.Observacion.ID_REQUERIDO,
                                    ArtefactosCodes.Observacion.ASESOR_REQUERIDO);
                });
    }
}
