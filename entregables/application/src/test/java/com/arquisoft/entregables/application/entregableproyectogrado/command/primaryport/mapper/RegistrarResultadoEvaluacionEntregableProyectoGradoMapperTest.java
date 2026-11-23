package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.RegistrarResultadoEvaluacionEntregableProyectoGradoCommand;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarResultadoEvaluacionEntregableProyectoGradoMapperTest {

    @Test
    void debeConstruirElObjetoDeAccion_cuandoMapeaElCommand() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var command = new RegistrarResultadoEvaluacionEntregableProyectoGradoCommand(entregable, "RECHAZADO");

        // Act
        var resultado = RegistrarResultadoEvaluacionEntregableProyectoGradoMapper.toDomain(command);

        // Assert
        assertThat(resultado.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(resultado.getEstadoEntregable()).isEqualTo(EstadoEntregable.RECHAZADO);
        assertThat(resultado.esRechazado()).isTrue();
    }
}
