package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.RegistrarEvaluacionesCualitativasAsesorCommand;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.RegistrarEvaluacionesCualitativasAsesorCommand.Par;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class RegistrarEvaluacionesCualitativasAsesorMapperTest {

    @Test
    void debeConstruirRegistroConUnaEvaluacionPorPar_cuandoMapeaElCommand() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var item1 = UtilUUID.generarNuevoUUID();
        var criterio1 = UtilUUID.generarNuevoUUID();
        var item2 = UtilUUID.generarNuevoUUID();
        var criterio2 = UtilUUID.generarNuevoUUID();
        var command = new RegistrarEvaluacionesCualitativasAsesorCommand(
                evaluacionAsesor, asesor, List.of(new Par<>(item1, criterio1), new Par<>(item2, criterio2)));

        // Act
        var registro = RegistrarEvaluacionesCualitativasAsesorMapper.toDomain(command);

        // Assert
        assertThat(registro.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(registro.getAsesor()).isEqualTo(asesor);
        assertThat(registro.getEvaluaciones()).hasSize(2);
        assertThat(registro.getEvaluaciones())
                .extracting(e -> e.getEvaluacionAsesor(), e -> e.getItem(), e -> e.getCriterio())
                .containsExactly(
                        tuple(evaluacionAsesor, item1, criterio1),
                        tuple(evaluacionAsesor, item2, criterio2));
    }
}
