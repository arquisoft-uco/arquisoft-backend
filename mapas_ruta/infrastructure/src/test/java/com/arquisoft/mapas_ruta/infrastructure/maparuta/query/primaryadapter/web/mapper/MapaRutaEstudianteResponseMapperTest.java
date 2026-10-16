package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaEstudianteReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MapaRutaEstudianteResponseMapperTest {

    @Test
    void debeTrasladarTodosLosCampos_cuandoConvierteElReadModelEnDto() {
        // Arrange
        var readModel = new MapaRutaEstudianteReadModel(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Titulo del proyecto",
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));

        // Act
        var dto = MapaRutaEstudianteResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.proyectoGrado()).isEqualTo(readModel.proyectoGrado());
        assertThat(dto.tituloProyecto()).isEqualTo("Titulo del proyecto");
        assertThat(dto.fechaInicio()).isEqualTo(LocalDate.of(2026, 10, 1));
        assertThat(dto.fechaFin()).isEqualTo(LocalDate.of(2026, 12, 1));
    }
}
