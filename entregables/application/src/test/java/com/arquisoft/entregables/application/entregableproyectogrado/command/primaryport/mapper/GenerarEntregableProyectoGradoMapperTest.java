package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.GenerarEntregableProyectoGradoCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GenerarEntregableProyectoGradoMapperTest {

    @Test
    void debeMapearProyectoYAsesor_cuandoSeConvierteElCommand() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = GenerarEntregableProyectoGradoCommand.crear(proyecto, asesor);

        // Act
        var domain = GenerarEntregableProyectoGradoMapper.toDomain(command);

        // Assert
        assertThat(domain.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(domain.getAsesor()).isEqualTo(asesor);
    }
}
