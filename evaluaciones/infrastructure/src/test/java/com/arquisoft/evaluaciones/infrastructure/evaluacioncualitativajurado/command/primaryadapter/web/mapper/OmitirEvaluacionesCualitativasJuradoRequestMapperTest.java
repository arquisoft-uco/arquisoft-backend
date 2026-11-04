package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web.dto.OmitirEvaluacionesCualitativasJuradoRequestDTO;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OmitirEvaluacionesCualitativasJuradoRequestMapperTest {

    @Test
    void debeArmarCommand_conLaEvaluacionJuradoDelPathYLosIdsDelBody() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var primera = UUID.randomUUID();
        var segunda = UUID.randomUUID();
        var dto = new OmitirEvaluacionesCualitativasJuradoRequestDTO(
                List.of(primera.toString(), segunda.toString()));

        // Act
        var command = OmitirEvaluacionesCualitativasJuradoRequestMapper.toCommand(dto, evaluacionJurado);

        // Assert
        assertThat(command.evaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(command.evaluaciones()).containsExactly(primera, segunda);
    }

    @Test
    void debeRechazarComoLoteVacio_cuandoElBodyNoTraeEvaluaciones() {
        // Arrange
        var dto = new OmitirEvaluacionesCualitativasJuradoRequestDTO(null);

        // Act & Assert
        assertThatThrownBy(() -> OmitirEvaluacionesCualitativasJuradoRequestMapper.toCommand(dto, UUID.randomUUID()))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.OmisionEvaluacionesCualitativasJurado.LOTE_VACIO));
    }
}
