package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstudiantesProyectoGradoCoordinadorRequestMapperTest {

    @Test
    void debeCrearQuery_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEstudiantesProyectoGradoCoordinadorRequestMapper.toQuery(proyectoGrado, coordinador);

        // Assert
        assertThat(query.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(query.coordinador()).isEqualTo(coordinador);
    }
}
