package com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.model.GenerarEvaluacionCommand;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GenerarEvaluacionMapperTest {

    @Test
    void debeArmarCompuestoConMismoEntregable_cuandoCommandValido() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var command = GenerarEvaluacionCommand.crear(
                entregable.toString(), proyectoGrado.toString(), "Sistema de riego", 2);

        // Act
        var generacion = GenerarEvaluacionMapper.toDomain(command);

        // Assert
        assertThat(generacion.getEntregable().getId()).isEqualTo(entregable);
        assertThat(generacion.getEntregable().getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(generacion.getEvaluacion().getEntregable()).isEqualTo(entregable);
        assertThat(generacion.getEvaluacion().getEstado()).isEqualTo(EstadoEvaluacion.PENDIENTE);
    }
}
