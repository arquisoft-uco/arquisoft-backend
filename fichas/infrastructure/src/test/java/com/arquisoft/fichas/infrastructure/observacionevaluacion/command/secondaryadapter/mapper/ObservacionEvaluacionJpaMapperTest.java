package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.ObservacionEvaluacionEntity;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.entity.ObservacionEvaluacionJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionEvaluacionJpaMapperTest {

    @Test
    void debeMapearEntityAJpaEntity_conLaEvaluacionComoIdPlano() {
        // Arrange
        var entity = new ObservacionEvaluacionEntity(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Observación válida");

        // Act
        var jpaEntity = ObservacionEvaluacionJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpaEntity.getId()).isEqualTo(entity.id());
        assertThat(jpaEntity.getEvaluacionFichaPerfilId()).isEqualTo(entity.evaluacionFichaPerfil());
        assertThat(jpaEntity.getObservacion()).isEqualTo(entity.observacion());
    }

    @Test
    void debeMapearJpaEntityAEntity_cuandoSeConvierte() {
        // Arrange
        var jpaEntity = ObservacionEvaluacionJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionFichaPerfilId(UtilUUID.generarNuevoUUID())
                .observacion("Observación válida")
                .build();

        // Act
        var entity = ObservacionEvaluacionJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(entity).isEqualTo(new ObservacionEvaluacionEntity(
                jpaEntity.getId(), jpaEntity.getEvaluacionFichaPerfilId(), "Observación válida"));
    }
}
