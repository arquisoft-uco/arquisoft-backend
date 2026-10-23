package com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.entity.TareaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TareaJpaMapperTest {

    @Test
    void debeMapearEntityAJpaEntity() {
        // Arrange
        var entity = new TareaEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Redactar capitulo", UtilUUID.generarNuevoUUID(), "NUEVA");

        // Act
        var jpa = TareaJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpa.getId()).isEqualTo(entity.id());
        assertThat(jpa.getMapaRutaId()).isEqualTo(entity.mapaRuta());
        assertThat(jpa.getDescripcion()).isEqualTo(entity.descripcion());
        assertThat(jpa.getAsesorId()).isEqualTo(entity.asesor());
        assertThat(jpa.getEstadoItemId()).isEqualTo("NUEVA");
    }
}
