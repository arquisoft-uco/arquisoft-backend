package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarAsesoresProyectoGradoEstudianteRequestMapperTest {

    @Test
    void debeArmarQuery_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarAsesoresProyectoGradoEstudianteRequestMapper.toQuery(proyectoGrado, estudiante);

        // Assert
        assertThat(query.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }
}
