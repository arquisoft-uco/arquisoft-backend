package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.RegistrarEvaluacionesCuantitativasJuradoCommand;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.RegistrarEvaluacionesCuantitativasJuradoCommand.Par;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class RegistrarEvaluacionesCuantitativasJuradoMapperTest {

    @Test
    void debeArmarRegistroConUnDomainPorPar_cuandoMapeaElCommand() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var item1 = UtilUUID.generarNuevoUUID();
        var item2 = UtilUUID.generarNuevoUUID();
        var command = new RegistrarEvaluacionesCuantitativasJuradoCommand(
                evaluacionJurado, jurado, List.of(new Par(item1, 100), new Par(item2, 200)));

        // Act
        var registro = RegistrarEvaluacionesCuantitativasJuradoMapper.toDomain(command);

        // Assert
        assertThat(registro.getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(registro.getJurado()).isEqualTo(jurado);
        assertThat(registro.getEvaluaciones())
                .extracting(e -> e.getEvaluacionJurado(), e -> e.getItem(), e -> e.getPuntaje())
                .containsExactly(
                        tuple(evaluacionJurado, item1, 100),
                        tuple(evaluacionJurado, item2, 200));
    }
}
