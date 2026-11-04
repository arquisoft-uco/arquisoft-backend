package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.primaryadapter.web.dto.AsignarJuradosEvaluacionRequestDTO;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarJuradosEvaluacionRequestMapperTest {

    @Test
    void debeConstruirElCommand_cuandoElDtoTraeJurados() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();

        // Act
        var command = AsignarJuradosEvaluacionRequestMapper.toCommand(
                new AsignarJuradosEvaluacionRequestDTO(List.of(jurado.toString())), evaluacion);

        // Assert
        assertThat(command.evaluacion()).isEqualTo(evaluacion);
        assertThat(command.jurados()).containsExactly(jurado);
    }

    @Test
    void debeRechazarLoteVacioSinReventar_cuandoLosJuradosSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> AsignarJuradosEvaluacionRequestMapper.toCommand(
                new AsignarJuradosEvaluacionRequestDTO(null), UtilUUID.generarNuevoUUID()))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.AsignacionJuradosEvaluacion.LOTE_VACIO));
    }
}
