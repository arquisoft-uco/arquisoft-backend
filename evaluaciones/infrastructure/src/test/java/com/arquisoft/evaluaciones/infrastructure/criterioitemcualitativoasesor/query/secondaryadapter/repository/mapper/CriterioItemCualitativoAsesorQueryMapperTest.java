package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.secondaryadapter.repository.CriterioItemCualitativoAsesorJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CriterioItemCualitativoAsesorQueryMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoConvierteAReadModel() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entity = CriterioItemCualitativoAsesorJpaQueryEntity.builder()
                .id(id)
                .nombre("Actitud")
                .descripcion("Evalúa la actitud")
                .build();

        // Act
        var resultado = CriterioItemCualitativoAsesorQueryMapper.toReadModel(entity);

        // Assert
        assertThat(resultado.id()).isEqualTo(id);
        assertThat(resultado.nombre()).isEqualTo("Actitud");
        assertThat(resultado.descripcion()).isEqualTo("Evalúa la actitud");
    }
}
