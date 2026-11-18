package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarAsesoresActualesProyectoGradoEstudianteRequestMapperTest {

    @Test
    void debeConstruirQuery_cuandoEstudianteValido() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarAsesoresActualesProyectoGradoEstudianteRequestMapper.toQuery(estudiante);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }
}
