package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarAsesoresProyectoGradoCoordinadorRequestMapperTest {

    @Test
    void debeCrearQuery_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarAsesoresProyectoGradoCoordinadorRequestMapper.toQuery(proyectoGrado, coordinador);

        // Assert
        assertThat(query.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(query.coordinador()).isEqualTo(coordinador);
    }
}
