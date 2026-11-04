package com.arquisoft.artefactos.application.observacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarObservacionesRevisionEstudianteQueryTest {

    @Test
    void debeCrearQuery_cuandoRevisionYEstudianteValidos() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarObservacionesRevisionEstudianteQuery.crear(revision, estudiante);

        // Assert
        assertThat(query.revisionAsesor()).isEqualTo(revision);
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoRevisionEsNula() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> ConsultarObservacionesRevisionEstudianteQuery.crear(null, estudiante))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    var errores = ex.getValidationResult().getErrores();
                    assertThat(errores).hasSize(1);
                    assertThat(errores.get(0).campo()).isEqualTo(ArtefactosFields.Observacion.REVISION_ASESOR);
                    assertThat(errores.get(0).codigoError())
                            .isEqualTo(ArtefactosCodes.Observacion.REVISION_ASESOR_ID_REQUERIDO);
                });
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoEstudianteEsNulo() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> ConsultarObservacionesRevisionEstudianteQuery.crear(revision, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    var errores = ex.getValidationResult().getErrores();
                    assertThat(errores).hasSize(1);
                    assertThat(errores.get(0).campo()).isEqualTo(ArtefactosFields.Observacion.ESTUDIANTE);
                    assertThat(errores.get(0).codigoError())
                            .isEqualTo(ArtefactosCodes.Observacion.ESTUDIANTE_REQUERIDO);
                });
    }
}
