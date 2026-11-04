package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository.mapper;

import com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository.MapaRutaJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MapaRutaQueryMapperTest {

    @Test
    void debeMapearCampoACampoSinCoordinador_cuandoConvierteLaEntidadDeLectura() {
        // Arrange
        var entity = MapaRutaJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .proyectoGradoId(UtilUUID.generarNuevoUUID())
                .tituloProyecto("Sistema de gestion")
                .coordinadorId(UtilUUID.generarNuevoUUID())
                .fechaInicio(LocalDate.of(2026, 10, 1))
                .fechaFin(LocalDate.of(2026, 12, 1))
                .build();

        // Act
        var readModel = MapaRutaQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.proyectoGrado()).isEqualTo(entity.getProyectoGradoId());
        assertThat(readModel.tituloProyecto()).isEqualTo("Sistema de gestion");
        assertThat(readModel.fechaInicio()).isEqualTo(LocalDate.of(2026, 10, 1));
        assertThat(readModel.fechaFin()).isEqualTo(LocalDate.of(2026, 12, 1));
    }
}
