package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.mapper;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.model.ConsultarEstadosEntregableProyectoGradoQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstadosEntregableProyectoGradoMapperTest {

    @Test
    void debeLlevarEntregableYAsesorAlCriteria() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var query = ConsultarEstadosEntregableProyectoGradoQuery.crear(entregable, asesor);

        // Act
        var criteria = ConsultarEstadosEntregableProyectoGradoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(criteria.asesor()).isEqualTo(asesor);
    }
}
