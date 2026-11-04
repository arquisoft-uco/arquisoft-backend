package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEstudiantesArtefactoQueryTest {

    @Test
    void debeCrearQuery_cuandoArtefactoValido() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEstudiantesArtefactoQuery.crear(artefacto);

        // Assert
        assertThat(query.artefacto()).isEqualTo(artefacto);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoArtefactoEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEstudiantesArtefactoQuery.crear(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    var errores = ex.getValidationResult().getErrores();
                    assertThat(errores).hasSize(1);
                    assertThat(errores.get(0).campo()).isEqualTo(ArtefactosFields.EstudianteArtefacto.ARTEFACTO);
                    assertThat(errores.get(0).codigoError())
                            .isEqualTo(ArtefactosCodes.EstudianteArtefacto.ARTEFACTO_ID_REQUERIDO);
                });
    }
}
