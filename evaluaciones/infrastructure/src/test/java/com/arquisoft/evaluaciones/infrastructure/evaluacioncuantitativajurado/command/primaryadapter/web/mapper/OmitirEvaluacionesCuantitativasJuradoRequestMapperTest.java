package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.dto.OmitirEvaluacionesCuantitativasJuradoRequestDTO;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OmitirEvaluacionesCuantitativasJuradoRequestMapperTest {

    @Test
    void debeArmarCommand_conLaEvaluacionJuradoDelPathYLosIdsDelBody() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var primera = UUID.randomUUID();
        var segunda = UUID.randomUUID();
        var dto = new OmitirEvaluacionesCuantitativasJuradoRequestDTO(
                List.of(primera.toString(), segunda.toString()));

        // Act
        var command = OmitirEvaluacionesCuantitativasJuradoRequestMapper.toCommand(dto, evaluacionJurado);

        // Assert
        assertThat(command.evaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(command.evaluaciones()).containsExactly(primera, segunda);
    }

    @Test
    void debeRechazarComoLoteVacio_cuandoElBodyNoTraeEvaluaciones() {
        // Arrange
        var dto = new OmitirEvaluacionesCuantitativasJuradoRequestDTO(null);

        // Act & Assert
        assertThatThrownBy(() -> OmitirEvaluacionesCuantitativasJuradoRequestMapper.toCommand(dto, UUID.randomUUID()))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.OmisionEvaluacionesCuantitativasJurado.LOTE_VACIO));
    }
}
