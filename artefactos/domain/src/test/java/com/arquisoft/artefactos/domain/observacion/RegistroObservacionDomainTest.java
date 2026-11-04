package com.arquisoft.artefactos.domain.observacion;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistroObservacionDomainTest {

    @Test
    void debeCrear_cuandoObservacionYAsesorPresentes() {
        // Arrange
        var observacion = ObservacionDomain.crear(UtilUUID.generarNuevoUUID(), "Detalle");
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var registro = RegistroObservacionDomain.crear(observacion, asesor);

        // Assert
        assertThat(registro.getObservacion()).isSameAs(observacion);
        assertThat(registro.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoObservacionYAsesorNulos() {
        // Act & Assert
        assertThatThrownBy(() -> RegistroObservacionDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError")
                            .contains(ArtefactosCodes.Observacion.ID_REQUERIDO,
                                    ArtefactosCodes.Observacion.ASESOR_REQUERIDO);
                });
    }
}
