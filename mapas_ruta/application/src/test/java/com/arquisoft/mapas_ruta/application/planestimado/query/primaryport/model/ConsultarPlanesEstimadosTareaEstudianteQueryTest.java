package com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class ConsultarPlanesEstimadosTareaEstudianteQueryTest {

    @Test
    void debeCrearLaConsulta_cuandoLaTareaEsValida() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarPlanesEstimadosTareaEstudianteQuery.crear(tarea, criterio);

        // Assert
        assertThat(query.tarea()).isEqualTo(tarea);
        assertThat(query.criterio()).isSameAs(criterio);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoLaTareaEsNula() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarPlanesEstimadosTareaEstudianteQuery.crear(null, criterio))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactly(tuple(MapasRutaFields.PlanEstimado.TAREA,
                                        MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TAREA_REQUERIDA)));
    }
}
