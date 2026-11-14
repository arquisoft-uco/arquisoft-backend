package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.entity.EvaluacionCualitativaAsesorEntity;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.EvaluacionCualitativaAsesorDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionCualitativaAsesorMapperTest {

    @Test
    void debeConvertirTodosLosCampos_cuandoMapeaDominioAEntity() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        var domain = EvaluacionCualitativaAsesorDomain.crear(evaluacionAsesor, item, criterio);

        // Act
        var entity = EvaluacionCualitativaAsesorMapper.toEntity(domain);

        // Assert
        assertThat(entity.id()).isEqualTo(domain.getId());
        assertThat(entity.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(entity.item()).isEqualTo(item);
        assertThat(entity.criterio()).isEqualTo(criterio);
    }

    @Test
    void debeMapearEntityADomain() {
        // Arrange
        var entity = new EvaluacionCualitativaAsesorEntity(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act
        var domain = EvaluacionCualitativaAsesorMapper.toDomain(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo(entity.id());
        assertThat(domain.getEvaluacionAsesor()).isEqualTo(entity.evaluacionAsesor());
        assertThat(domain.getItem()).isEqualTo(entity.item());
        assertThat(domain.getCriterio()).isEqualTo(entity.criterio());
    }
}
