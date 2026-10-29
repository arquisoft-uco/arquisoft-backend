package com.arquisoft.artefactos.application.artefacto.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarArtefactosEstudianteQueryTest {

    @Test
    void debeCrearQuery_cuandoEstudianteValido() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarArtefactosEstudianteQuery.crear(estudiante, criterio);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
        assertThat(query.criterio()).isSameAs(criterio);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoEstudianteEsNulo() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarArtefactosEstudianteQuery.crear(null, criterio))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    var errores = ex.getValidationResult().getErrores();
                    assertThat(errores).hasSize(1);
                    assertThat(errores.get(0).campo()).isEqualTo(ArtefactosFields.Artefacto.ESTUDIANTE);
                    assertThat(errores.get(0).codigoError()).isEqualTo(ArtefactosCodes.Artefacto.ESTUDIANTE_REQUERIDO);
                });
    }
}
