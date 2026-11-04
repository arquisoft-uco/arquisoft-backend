package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.dto.AgregarPlanEstimadoRequestDTO;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarPlanEstimadoRequestMapperTest {

    @Test
    void debeMapearACommand_cuandoDtoValido() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var dto = new AgregarPlanEstimadoRequestDTO(4, "Revisar");

        // Act
        var command = AgregarPlanEstimadoRequestMapper.toCommand(dto, tarea);

        // Assert
        assertThat(command.tarea()).isEqualTo(tarea);
        assertThat(command.tiempoEstimadoDias()).isEqualTo(4);
        assertThat(command.descripcionEstimado()).isEqualTo("Revisar");
    }

    @Test
    void debeLanzarValidacion_cuandoTiempoAusente() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var dto = new AgregarPlanEstimadoRequestDTO(null, null);

        // Act & Assert
        assertThatThrownBy(() -> AgregarPlanEstimadoRequestMapper.toCommand(dto, tarea))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactly(MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TIEMPO_REQUERIDO));
    }
}
