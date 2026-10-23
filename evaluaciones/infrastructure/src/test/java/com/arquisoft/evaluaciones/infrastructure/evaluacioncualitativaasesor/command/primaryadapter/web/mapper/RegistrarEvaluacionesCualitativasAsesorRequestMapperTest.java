package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web.dto.RegistrarEvaluacionesCualitativasAsesorRequestDTO;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RegistrarEvaluacionesCualitativasAsesorRequestMapperTest {

    @Test
    void debeRechazarComoLoteVacio_cuandoLaListaEsNula() {
        // Arrange
        var dto = new RegistrarEvaluacionesCualitativasAsesorRequestDTO(null);
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCualitativasAsesorRequestMapper.toCommand(
                dto, evaluacionAsesor, asesor))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCualitativasAsesor.LOTE_VACIO));
    }

    @Test
    void debeRechazarConCamposIndexados_cuandoUnParEsNulo() {
        // Arrange
        var dto = new RegistrarEvaluacionesCualitativasAsesorRequestDTO(Arrays.asList((
                RegistrarEvaluacionesCualitativasAsesorRequestDTO.ParDTO) null));
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCualitativasAsesorRequestMapper.toCommand(
                dto, evaluacionAsesor, asesor))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple("evaluaciones[0].item",
                                                EvaluacionesCodes.EvaluacionCualitativaAsesor.ITEM_REQUERIDO),
                                        tuple("evaluaciones[0].criterio",
                                                EvaluacionesCodes.EvaluacionCualitativaAsesor.CRITERIO_REQUERIDO)));
    }
}
