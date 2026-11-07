package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesCualitativasAsesorEstudianteRequestMapperTest {

    @Test
    void debeCrearQuery_cuandoDatosValidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEvaluacionesCualitativasAsesorEstudianteRequestMapper.toQuery(
                evaluacionAsesor, estudiante);

        // Assert
        assertThat(query.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }
}
