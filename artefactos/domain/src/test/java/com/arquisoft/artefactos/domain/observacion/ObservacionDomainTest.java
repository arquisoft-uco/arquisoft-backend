package com.arquisoft.artefactos.domain.observacion;

import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();

        // Act
        var observacion = ObservacionDomain.crear(revision, "  Falta la bibliografia  ");

        // Assert
        assertThat(observacion.getId()).isNotNull();
        assertThat(observacion.getRevisionAsesor()).isEqualTo(revision);
        assertThat(observacion.getDescripcion()).isEqualTo("Falta la bibliografia");
        assertThat(observacion.getEstado()).isEqualTo(EstadoObservacion.NUEVA);
    }

    @Test
    void debeAcumularFieldErrors_cuandoRevisionNulaYDescripcionEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> ObservacionDomain.crear(null, "   "))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError")
                            .contains(ArtefactosCodes.Observacion.REVISION_ASESOR_ID_REQUERIDO,
                                    ArtefactosCodes.Observacion.DESCRIPCION_REQUERIDA);
                });
    }

    @Test
    void debeRechazar_cuandoDescripcionSuperaMaximoTrasRecortar() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var excedida = "a".repeat(ArtefactosLimits.Observacion.DESCRIPCION_MAX + 1);
        var limite = " " + "a".repeat(ArtefactosLimits.Observacion.DESCRIPCION_MAX) + " ";

        // Act & Assert
        assertThatThrownBy(() -> ObservacionDomain.crear(revision, excedida))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError")
                            .contains(ArtefactosCodes.Observacion.DESCRIPCION_LONGITUD_EXCEDIDA);
                });
        assertThat(ObservacionDomain.crear(revision, limite).getDescripcion()).hasSize(1000);
    }
}
