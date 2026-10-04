package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionEstudianteQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarObservacionesEvaluacionEstudianteMapperTest {

    @Test
    void debeCopiarEvaluacionYEstudiante_cuandoMapeaACriteria() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarObservacionesEvaluacionEstudianteQuery.crear(evaluacionFichaPerfil, estudiante);

        // Act
        var criteria = ConsultarObservacionesEvaluacionEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.evaluacionFichaPerfil()).isEqualTo(evaluacionFichaPerfil);
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
    }
}
