package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarParticipacionesArtefactoQueryTest {

    @Test
    void debeCrearQuery_cuandoEstudianteValido() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarParticipacionesArtefactoQuery.crear(estudiante);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoEstudianteEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarParticipacionesArtefactoQuery.crear(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    var errores = ex.getValidationResult().getErrores();
                    assertThat(errores).hasSize(1);
                    assertThat(errores.get(0).campo()).isEqualTo(ArtefactosFields.EstudianteArtefacto.ESTUDIANTE);
                    assertThat(errores.get(0).codigoError())
                            .isEqualTo(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_ID_REQUERIDO);
                });
    }
}
