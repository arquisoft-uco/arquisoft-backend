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
    void debeCrearQuery_cuandoRevisionValida() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarObservacionesRevisionQuery.crear(revision);

        // Assert
        assertThat(query.revisionAsesor()).isEqualTo(revision);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoRevisionEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarObservacionesRevisionQuery.crear(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    var errores = ex.getValidationResult().getErrores();
                    assertThat(errores).hasSize(1);
                    assertThat(errores.get(0).campo()).isEqualTo(ArtefactosFields.Observacion.REVISION_ASESOR);
                    assertThat(errores.get(0).codigoError())
                            .isEqualTo(ArtefactosCodes.Observacion.REVISION_ASESOR_ID_REQUERIDO);
                });
    }
}
