package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.RegistrarEvaluacionesCuantitativasAsesorCommand;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.RegistrarEvaluacionesCuantitativasAsesorCommand.Par;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class RegistrarEvaluacionesCuantitativasAsesorMapperTest {

    @Test
    void debeConstruirRegistroConUnaEvaluacionPorPar_cuandoMapeaElCommand() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var item1 = UtilUUID.generarNuevoUUID();
        var item2 = UtilUUID.generarNuevoUUID();
        var command = new RegistrarEvaluacionesCuantitativasAsesorCommand(
                evaluacionAsesor, asesor, List.of(new Par(item1, 10), new Par(item2, 20)));

        // Act
        var registro = RegistrarEvaluacionesCuantitativasAsesorMapper.toDomain(command);

        // Assert
        assertThat(registro.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(registro.getAsesor()).isEqualTo(asesor);
        assertThat(registro.getEvaluaciones())
                .extracting(e -> e.getEvaluacionAsesor(), e -> e.getItem(), e -> e.getPuntaje())
                .containsExactly(
                        tuple(evaluacionAsesor, item1, 10),
                        tuple(evaluacionAsesor, item2, 20));
    }
}
