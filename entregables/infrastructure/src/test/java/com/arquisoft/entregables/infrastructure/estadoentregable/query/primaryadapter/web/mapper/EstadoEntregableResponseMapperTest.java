package com.arquisoft.entregables.infrastructure.estadoentregable.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.estadoentregable.query.readmodel.EstadoEntregableReadModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoEntregableResponseMapperTest {

    @Test
    void debeMapearLosTresCampos_cuandoRecibeElReadModel() {
        // Arrange
        var readModel = new EstadoEntregableReadModel("EN_EVALUACION", "En Evaluacion", "Evaluando");

        // Act
        var dto = EstadoEntregableResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo("EN_EVALUACION");
        assertThat(dto.nombre()).isEqualTo("En Evaluacion");
        assertThat(dto.descripcion()).isEqualTo("Evaluando");
    }
}
