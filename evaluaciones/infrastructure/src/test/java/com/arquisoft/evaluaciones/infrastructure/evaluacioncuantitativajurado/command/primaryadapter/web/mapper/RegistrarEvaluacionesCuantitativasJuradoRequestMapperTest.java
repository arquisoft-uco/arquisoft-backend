package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.RegistrarEvaluacionesCuantitativasJuradoCommand.Par;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.dto.RegistrarEvaluacionesCuantitativasJuradoRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.dto.RegistrarEvaluacionesCuantitativasJuradoRequestDTO.EvaluacionDTO;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RegistrarEvaluacionesCuantitativasJuradoRequestMapperTest {

    @Test
    void debeArmarCommand_conLaEvaluacionJuradoDelPathYElJuradoDeLaIdentidad() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var dto = new RegistrarEvaluacionesCuantitativasJuradoRequestDTO(
                List.of(new EvaluacionDTO(item.toString(), 400)));

        // Act
        var command = RegistrarEvaluacionesCuantitativasJuradoRequestMapper.toCommand(dto, evaluacionJurado, jurado);

        // Assert
        assertThat(command.evaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(command.jurado()).isEqualTo(jurado);
        assertThat(command.evaluaciones()).containsExactly(new Par(item, 400));
    }

    @Test
    void debeRechazarComoLoteVacio_cuandoLaListaNoLlega() {
        // Arrange
        var dto = new RegistrarEvaluacionesCuantitativasJuradoRequestDTO(null);
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCuantitativasJuradoRequestMapper.toCommand(
                dto, evaluacionJurado, jurado))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCuantitativasJurado.LOTE_VACIO));
    }

    @Test
    void debeRechazarConErroresIndexados_cuandoUnParEsNulo() {
        // Arrange
        var pares = new ArrayList<EvaluacionDTO>();
        pares.add(null);
        var dto = new RegistrarEvaluacionesCuantitativasJuradoRequestDTO(pares);
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCuantitativasJuradoRequestMapper.toCommand(
                dto, evaluacionJurado, jurado))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple("evaluaciones[0].item",
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.ITEM_REQUERIDO),
                                        tuple("evaluaciones[0].puntaje",
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_REQUERIDO)));
    }
}
