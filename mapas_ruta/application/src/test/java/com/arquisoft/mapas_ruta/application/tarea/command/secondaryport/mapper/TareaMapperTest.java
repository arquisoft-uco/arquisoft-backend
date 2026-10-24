package com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.entity.TareaEntity;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TareaMapperTest {

    @Test
    void debeMapearADomainAEntity_conEstadoNueva() {
        // Arrange
        var tarea = TareaDomain.crear(UtilUUID.generarNuevoUUID(), "Redactar capitulo", UtilUUID.generarNuevoUUID());

        // Act
        var entity = TareaMapper.toEntity(tarea);

        // Assert
        assertThat(entity.id()).isEqualTo(tarea.getId());
        assertThat(entity.mapaRuta()).isEqualTo(tarea.getMapaRuta());
        assertThat(entity.descripcion()).isEqualTo("Redactar capitulo");
        assertThat(entity.asesor()).isEqualTo(tarea.getAsesor());
        assertThat(entity.estadoItem()).isEqualTo("NUEVA");
    }

    @Test
    void debeMapearEntityADomain_reconstruyendoElEstado() {
        // Arrange
        var entity = new TareaEntity(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Redactar capitulo",
                UtilUUID.generarNuevoUUID(), "EN_PROGRESO");

        // Act
        var tarea = TareaMapper.toDomain(entity);

        // Assert
        assertThat(tarea.getId()).isEqualTo(entity.id());
        assertThat(tarea.getMapaRuta()).isEqualTo(entity.mapaRuta());
        assertThat(tarea.getDescripcion()).isEqualTo("Redactar capitulo");
        assertThat(tarea.getAsesor()).isEqualTo(entity.asesor());
        assertThat(tarea.getEstadoItem()).isEqualTo(EstadoItem.EN_PROGRESO);
    }
}
