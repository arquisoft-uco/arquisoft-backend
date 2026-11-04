package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarProyectosGradoEstudianteRequestMapperTest {

    @Test
    void debeConstruirQuery_cuandoEstudianteValido() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarProyectosGradoEstudianteRequestMapper.toQuery(estudiante);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }
}
