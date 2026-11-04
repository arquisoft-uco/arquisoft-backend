package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresProyectoGradoCoordinadorQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarAsesoresProyectoGradoCoordinadorMapperTest {

    @Test
    void debeConstruirCriteria_cuandoQueryValida() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var query = ConsultarAsesoresProyectoGradoCoordinadorQuery.crear(proyectoGrado, coordinador);

        // Act
        var criteria = ConsultarAsesoresProyectoGradoCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(criteria.coordinador()).isEqualTo(coordinador);
    }
}
