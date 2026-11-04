package com.arquisoft.mapas_ruta.infrastructure.estadoitem.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.estadoitem.query.readmodel.EstadoItemReadModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoItemResponseMapperTest {

    @Test
    void debeMapearLosTresCampos_cuandoRecibeElReadModel() {
        // Arrange
        var readModel = new EstadoItemReadModel("EN_PROGRESO", "En progreso", "En curso");

        // Act
        var dto = EstadoItemResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo("EN_PROGRESO");
        assertThat(dto.nombre()).isEqualTo("En progreso");
        assertThat(dto.descripcion()).isEqualTo("En curso");
    }
}
