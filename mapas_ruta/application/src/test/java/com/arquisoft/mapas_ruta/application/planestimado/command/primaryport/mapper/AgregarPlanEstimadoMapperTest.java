package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.AgregarPlanEstimadoCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AgregarPlanEstimadoMapperTest {

    @Test
    void debeMapearADomain_cuandoCommandValido() {
        // Arrange
        var command = new AgregarPlanEstimadoCommand(UtilUUID.generarNuevoUUID(), 4, "Revisar");

        // Act
        var plan = AgregarPlanEstimadoMapper.toDomain(command);

        // Assert
        assertThat(plan.getId()).isNotNull();
        assertThat(plan.getTarea()).isEqualTo(command.tarea());
        assertThat(plan.getTiempoEstimadoDias()).isEqualTo(4);
        assertThat(plan.getDescripcionEstimado()).isEqualTo("Revisar");
    }
}
