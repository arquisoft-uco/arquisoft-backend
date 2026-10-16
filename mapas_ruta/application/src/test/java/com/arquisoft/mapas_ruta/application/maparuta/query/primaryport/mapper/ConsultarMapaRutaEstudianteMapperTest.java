package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapaRutaEstudianteQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarMapaRutaEstudianteMapperTest {

    @Test
    void debeTrasladarElEstudianteAlCriteria_cuandoConvierteLaConsulta() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarMapaRutaEstudianteQuery.crear(estudiante);

        // Act
        var criteria = ConsultarMapaRutaEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
    }
}
