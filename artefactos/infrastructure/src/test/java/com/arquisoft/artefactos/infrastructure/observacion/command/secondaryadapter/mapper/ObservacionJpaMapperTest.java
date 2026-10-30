package com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.observacion.command.secondaryport.entity.ObservacionEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionJpaMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoSeConvierteALaEntidadJpa() {
        // Arrange
        var entity = new ObservacionEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Detalle", "NUEVA");

        // Act
        var jpa = ObservacionJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpa.getId()).isEqualTo(entity.id());
        assertThat(jpa.getRevisionAsesor()).isEqualTo(entity.revisionAsesor());
        assertThat(jpa.getDescripcion()).isEqualTo("Detalle");
        assertThat(jpa.getEstado()).isEqualTo("NUEVA");
    }
}
