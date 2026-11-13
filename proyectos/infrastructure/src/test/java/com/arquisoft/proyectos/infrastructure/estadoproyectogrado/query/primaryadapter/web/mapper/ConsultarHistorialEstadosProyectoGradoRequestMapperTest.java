package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarHistorialEstadosProyectoGradoRequestMapperTest {

    @Test
    void debeConstruirQuery_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarHistorialEstadosProyectoGradoRequestMapper.toQuery(proyectoGrado, usuario);

        // Assert
        assertThat(query.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(query.usuario()).isEqualTo(usuario);
    }
}
