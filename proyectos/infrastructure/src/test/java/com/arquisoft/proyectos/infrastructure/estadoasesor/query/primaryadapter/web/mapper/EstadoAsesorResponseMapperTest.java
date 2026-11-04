package com.arquisoft.proyectos.infrastructure.estadoasesor.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoAsesorResponseMapperTest {

    @Test
    void debeMapearCamposDelReadModel_cuandoSeConvierte() {
        // Arrange
        var readModel = new EstadoAsesorReadModel("ASIGNADO", "Asignado", "Asesor asignado");

        // Act
        var respuesta = EstadoAsesorResponseMapper.toResponse(readModel);

        // Assert
        assertThat(respuesta.id()).isEqualTo("ASIGNADO");
        assertThat(respuesta.nombre()).isEqualTo("Asignado");
        assertThat(respuesta.descripcion()).isEqualTo("Asesor asignado");
    }
}
