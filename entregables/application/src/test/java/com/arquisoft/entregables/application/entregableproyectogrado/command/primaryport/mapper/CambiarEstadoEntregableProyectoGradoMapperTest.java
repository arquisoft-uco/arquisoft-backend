package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.CambiarEstadoEntregableProyectoGradoCommand;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarEstadoEntregableProyectoGradoMapperTest {

    @Test
    void debeMapearElCommandAlObjetoDeAccion_cuandoSeConvierte() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var command = CambiarEstadoEntregableProyectoGradoCommand.crear(entregable, "CANCELADO");

        // Act
        var cambio = CambiarEstadoEntregableProyectoGradoMapper.toDomain(command);

        // Assert
        assertThat(cambio.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(cambio.getEstadoEntregable()).isEqualTo(EstadoEntregable.CANCELADO);
    }
}
