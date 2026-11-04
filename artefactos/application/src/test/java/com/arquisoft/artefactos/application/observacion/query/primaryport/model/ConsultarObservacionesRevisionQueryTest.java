package com.arquisoft.artefactos.application.observacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarObservacionesRevisionQueryTest {

    @Test
    void debeCrearQuery_cuandoRevisionYAsesorValidos() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarObservacionesRevisionQuery.crear(revision, asesor);

        // Assert
        assertThat(query.revisionAsesor()).isEqualTo(revision);
        assertThat(query.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoRevisionEsNula() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> ConsultarObservacionesRevisionQuery.crear(null, asesor))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    var errores = ex.getValidationResult().getErrores();
                    assertThat(errores).hasSize(1);
                    assertThat(errores.get(0).campo()).isEqualTo(ArtefactosFields.Observacion.REVISION_ASESOR);
                    assertThat(errores.get(0).codigoError())
                            .isEqualTo(ArtefactosCodes.Observacion.REVISION_ASESOR_ID_REQUERIDO);
                });
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoAsesorEsNulo() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> ConsultarObservacionesRevisionQuery.crear(revision, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    var errores = ex.getValidationResult().getErrores();
                    assertThat(errores).hasSize(1);
                    assertThat(errores.get(0).campo()).isEqualTo(ArtefactosFields.Observacion.ASESOR);
                    assertThat(errores.get(0).codigoError())
                            .isEqualTo(ArtefactosCodes.Observacion.ASESOR_REQUERIDO);
                });
    }
}
