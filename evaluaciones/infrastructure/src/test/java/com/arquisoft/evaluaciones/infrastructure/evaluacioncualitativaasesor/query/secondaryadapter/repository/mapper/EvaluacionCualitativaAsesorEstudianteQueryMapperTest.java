package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.secondaryadapter.repository.EvaluacionCualitativaAsesorEstudianteJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionCualitativaAsesorEstudianteQueryMapperTest {

    @Test
    void debeMapearReadModel_desdeJpaQueryEntity() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var itemId = UtilUUID.generarNuevoUUID();
        var criterioId = UtilUUID.generarNuevoUUID();
        var entity = EvaluacionCualitativaAsesorEstudianteJpaQueryEntity.builder()
                .id(id)
                .evaluacionAsesorId(UtilUUID.generarNuevoUUID())
                .itemId(itemId).itemNombre("Claridad").itemDescripcion("Evalua la claridad")
                .criterioId(criterioId).criterioNombre("Excelente").criterioDescripcion("Cumple todo")
                .build();

        // Act
        var readModel = EvaluacionCualitativaAsesorEstudianteQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(id);
        assertThat(readModel.item().id()).isEqualTo(itemId);
        assertThat(readModel.item().nombre()).isEqualTo("Claridad");
        assertThat(readModel.item().descripcion()).isEqualTo("Evalua la claridad");
        assertThat(readModel.criterio().id()).isEqualTo(criterioId);
        assertThat(readModel.criterio().nombre()).isEqualTo("Excelente");
        assertThat(readModel.criterio().descripcion()).isEqualTo("Cumple todo");
    }
}
