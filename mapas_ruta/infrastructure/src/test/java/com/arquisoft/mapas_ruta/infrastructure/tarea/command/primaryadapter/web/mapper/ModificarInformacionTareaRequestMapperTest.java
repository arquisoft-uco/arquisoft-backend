package com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.dto.ModificarInformacionTareaRequestDTO;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarInformacionTareaRequestMapperTest {

    @Test
    void debeMapearACommand_cuandoDtoValido() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var dto = new ModificarInformacionTareaRequestDTO("Redactar capitulo", asesor.toString());

        // Act
        var command = ModificarInformacionTareaRequestMapper.toCommand(dto, tarea);

        // Assert
        assertThat(command.tarea()).isEqualTo(tarea);
        assertThat(command.descripcion()).isEqualTo("Redactar capitulo");
        assertThat(command.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeFallar_cuandoAsesorNoEsUuid() {
        // Arrange
        var dto = new ModificarInformacionTareaRequestDTO("Redactar capitulo", "no-uuid");
        var tarea = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> ModificarInformacionTareaRequestMapper.toCommand(dto, tarea))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactly(MapasRutaCodes.Tarea.ASESOR_INVALIDO));
    }
}
