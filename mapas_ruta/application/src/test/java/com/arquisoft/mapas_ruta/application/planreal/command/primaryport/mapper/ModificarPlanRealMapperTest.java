package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.ModificarPlanRealCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarPlanRealMapperTest {

    @Test
    void debeMapearADomain_cuandoCommandValido() {
        // Arrange
        var command = new ModificarPlanRealCommand(UtilUUID.generarNuevoUUID(), 4, "Revisar");

        // Act
        var modificacion = ModificarPlanRealMapper.toDomain(command);

        // Assert
        assertThat(modificacion.getPlanReal()).isEqualTo(command.planReal());
        assertThat(modificacion.getTiempoRealDias()).isEqualTo(4);
        assertThat(modificacion.getDescripcionReal()).isEqualTo("Revisar");
    }
}
