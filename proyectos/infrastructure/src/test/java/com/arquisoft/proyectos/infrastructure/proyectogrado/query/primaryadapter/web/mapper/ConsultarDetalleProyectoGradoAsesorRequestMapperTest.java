package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarDetalleProyectoGradoAsesorRequestMapperTest {

    @Test
    void debeCrearQuery_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarDetalleProyectoGradoAsesorRequestMapper.toQuery(proyectoGrado, asesor);

        // Assert
        assertThat(query.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(query.asesor()).isEqualTo(asesor);
    }
}
