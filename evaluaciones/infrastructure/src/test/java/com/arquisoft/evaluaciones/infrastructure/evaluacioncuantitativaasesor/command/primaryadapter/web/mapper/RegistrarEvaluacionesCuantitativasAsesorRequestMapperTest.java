package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.dto.RegistrarEvaluacionesCuantitativasAsesorRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.dto.RegistrarEvaluacionesCuantitativasAsesorRequestDTO.EvaluacionDTO;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RegistrarEvaluacionesCuantitativasAsesorRequestMapperTest {

    @Test
    void debeRechazarComoLoteVacio_cuandoLaListaEsNula() {
        // Arrange
        var dto = new RegistrarEvaluacionesCuantitativasAsesorRequestDTO(null);
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCuantitativasAsesorRequestMapper.toCommand(
                dto, evaluacionAsesor, asesor))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCuantitativasAsesor.LOTE_VACIO));
    }

    @Test
    void debeRechazarConCamposIndexados_cuandoUnElementoEsNulo() {
        // Arrange
        var dto = new RegistrarEvaluacionesCuantitativasAsesorRequestDTO(Arrays.asList((EvaluacionDTO) null));
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCuantitativasAsesorRequestMapper.toCommand(
                dto, evaluacionAsesor, asesor))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple("evaluaciones[0].item",
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ITEM_REQUERIDO),
                                        tuple("evaluaciones[0].puntaje",
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_REQUERIDO)));
    }
}
