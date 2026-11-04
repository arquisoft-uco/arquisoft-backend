package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GenerarEntregableProyectoGradoRequestMapperTest {

    @Test
    void debeUsarElIdDeLaIdentidadComoAsesor_cuandoSeConvierteACommand() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var identidad = new IdentidadActual(asesor, "a@uco.edu.co", "asesor", List.of(), "jti", null);

        // Act
        var command = GenerarEntregableProyectoGradoRequestMapper.toCommand(proyecto, identidad);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
        assertThat(command.asesor()).isEqualTo(asesor);
    }
}
