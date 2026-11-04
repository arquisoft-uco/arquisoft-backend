package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.EvaluacionCuantitativaAsesorDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionCuantitativaAsesorMapperTest {

    @Test
    void debeConvertirTodosLosCampos_cuandoMapeaDominioAEntity() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var domain = EvaluacionCuantitativaAsesorDomain.crear(evaluacionAsesor, item, 77);

        // Act
        var entity = EvaluacionCuantitativaAsesorMapper.toEntity(domain);

        // Assert
        assertThat(entity.id()).isEqualTo(domain.getId());
        assertThat(entity.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(entity.item()).isEqualTo(item);
        assertThat(entity.puntaje()).isEqualTo(77);
    }
}
