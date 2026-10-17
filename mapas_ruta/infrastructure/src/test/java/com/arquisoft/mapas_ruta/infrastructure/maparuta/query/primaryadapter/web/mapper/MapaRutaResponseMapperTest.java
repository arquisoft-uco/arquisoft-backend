package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MapaRutaResponseMapperTest {

    @Test
    void debeMapearCampoACampo_cuandoConvierteElReadModel() {
        // Arrange
        var readModel = new MapaRutaReadModel(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Sistema de gestion", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));

        // Act
        var dto = MapaRutaResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.proyectoGrado()).isEqualTo(readModel.proyectoGrado());
        assertThat(dto.tituloProyecto()).isEqualTo("Sistema de gestion");
        assertThat(dto.fechaInicio()).isEqualTo(LocalDate.of(2026, 10, 1));
        assertThat(dto.fechaFin()).isEqualTo(LocalDate.of(2026, 12, 1));
    }
}
