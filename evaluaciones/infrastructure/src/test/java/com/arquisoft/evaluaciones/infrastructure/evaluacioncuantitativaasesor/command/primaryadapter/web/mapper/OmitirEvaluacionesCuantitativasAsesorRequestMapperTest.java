package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.mapper;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OmitirEvaluacionesCuantitativasAsesorRequestMapperTest {

    @Test
    void debeMapear_cuandoItemsPresentes() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();

        // Act
        var command = OmitirEvaluacionesCuantitativasAsesorRequestMapper.toCommand(
                List.of(item.toString()), evaluacionAsesor, asesor);

        // Assert
        assertThat(command.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.items()).containsExactly(item);
    }

    @Test
    void debeLanzar400_cuandoItemsNulos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> OmitirEvaluacionesCuantitativasAsesorRequestMapper.toCommand(
                null, evaluacionAsesor, asesor))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.OmisionEvaluacionesCuantitativasAsesor.LOTE_VACIO));
    }
}
