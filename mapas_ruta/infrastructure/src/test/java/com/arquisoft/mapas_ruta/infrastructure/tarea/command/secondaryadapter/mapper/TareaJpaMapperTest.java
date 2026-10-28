package com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.entity.TareaEntity;
import com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.entity.TareaJpaEntity;
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

    @Test
    void debeMapearJpaEntityAEntity() {
        // Arrange
        var jpa = TareaJpaEntity.builder().id(UtilUUID.generarNuevoUUID())
                .mapaRutaId(UtilUUID.generarNuevoUUID()).descripcion("Redactar capitulo")
                .asesorId(UtilUUID.generarNuevoUUID()).estadoItemId("EN_PROGRESO").build();

        // Act
        var entity = TareaJpaMapper.toEntity(jpa);

        // Assert
        assertThat(entity.id()).isEqualTo(jpa.getId());
        assertThat(entity.mapaRuta()).isEqualTo(jpa.getMapaRutaId());
        assertThat(entity.descripcion()).isEqualTo("Redactar capitulo");
        assertThat(entity.asesor()).isEqualTo(jpa.getAsesorId());
        assertThat(entity.estadoItem()).isEqualTo("EN_PROGRESO");
    }
}
