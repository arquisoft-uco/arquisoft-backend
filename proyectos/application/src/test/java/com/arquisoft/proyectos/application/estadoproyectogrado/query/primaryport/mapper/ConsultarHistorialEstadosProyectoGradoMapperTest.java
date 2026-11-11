package com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.model.ConsultarHistorialEstadosProyectoGradoQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarHistorialEstadosProyectoGradoMapperTest {

    @Test
    void debeMapearCriteria_cuandoQueryValida() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var usuario = UtilUUID.generarNuevoUUID();
        var query = ConsultarHistorialEstadosProyectoGradoQuery.crear(proyectoGrado, usuario);

        // Act
        var criteria = ConsultarHistorialEstadosProyectoGradoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(criteria.usuario()).isEqualTo(usuario);
    }
}
