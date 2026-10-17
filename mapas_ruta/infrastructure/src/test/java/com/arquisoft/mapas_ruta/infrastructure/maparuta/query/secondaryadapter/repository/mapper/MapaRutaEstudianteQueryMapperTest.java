package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository.mapper;

import com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository.MapaRutaEstudianteJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MapaRutaEstudianteQueryMapperTest {

    @Test
    void debeTrasladarTodosLosCampos_cuandoConvierteLaEntidadEnReadModel() {
        // Arrange
        var entity = MapaRutaEstudianteJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .proyectoGradoId(UtilUUID.generarNuevoUUID())
                .tituloProyecto("Titulo del proyecto")
                .fechaInicio(LocalDate.of(2026, 10, 1))
                .fechaFin(LocalDate.of(2026, 12, 1))
                .build();

        // Act
        var readModel = MapaRutaEstudianteQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.proyectoGrado()).isEqualTo(entity.getProyectoGradoId());
        assertThat(readModel.tituloProyecto()).isEqualTo("Titulo del proyecto");
        assertThat(readModel.fechaInicio()).isEqualTo(LocalDate.of(2026, 10, 1));
        assertThat(readModel.fechaFin()).isEqualTo(LocalDate.of(2026, 12, 1));
    }
}
