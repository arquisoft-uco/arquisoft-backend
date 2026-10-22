package com.arquisoft.artefactos.infrastructure.estadorevisionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.estadorevisionasesor.query.readmodel.EstadoRevisionAsesorReadModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoRevisionAsesorResponseMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoConvierteElReadModel() {
        // Arrange
        var readModel = new EstadoRevisionAsesorReadModel("CERRADA", "Cerrada", "El asesor cerró la revisión");

        // Act
        var resultado = EstadoRevisionAsesorResponseMapper.toResponse(readModel);

        // Assert
        assertThat(resultado.id()).isEqualTo("CERRADA");
        assertThat(resultado.nombre()).isEqualTo("Cerrada");
        assertThat(resultado.descripcion()).isEqualTo("El asesor cerró la revisión");
    }
}
