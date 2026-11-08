package com.arquisoft.mapas_ruta.application.planreal.query.primaryport.model;

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

class ConsultarPlanesRealesProyectoGradoAsesorQueryTest {

    @Test
    void debeCrearQuery_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarPlanesRealesProyectoGradoAsesorQuery.crear(proyectoGrado, criterio);

        // Assert
        assertThat(query.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(query.criterio()).isSameAs(criterio);
    }

    @Test
    void debeLanzarValidacionEntrada_cuandoProyectoGradoEsNulo() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarPlanesRealesProyectoGradoAsesorQuery.crear(null, criterio))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactly(tuple(MapasRutaFields.PlanReal.PROYECTO_GRADO,
                                        MapasRutaCodes.PlanReal.PLAN_REAL_PROYECTO_GRADO_REQUERIDO)));
    }
}
