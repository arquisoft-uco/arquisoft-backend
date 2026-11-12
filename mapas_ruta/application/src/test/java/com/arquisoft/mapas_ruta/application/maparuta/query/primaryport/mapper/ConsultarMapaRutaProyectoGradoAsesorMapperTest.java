package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapaRutaProyectoGradoAsesorQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarMapaRutaProyectoGradoAsesorMapperTest {

    @Test
    void debeTrasladarElProyectoGradoAlCriteria_cuandoConvierteLaConsulta() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var query = ConsultarMapaRutaProyectoGradoAsesorQuery.crear(proyectoGrado);

        // Act
        var criteria = ConsultarMapaRutaProyectoGradoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.proyectoGrado()).isEqualTo(proyectoGrado);
    }
}
