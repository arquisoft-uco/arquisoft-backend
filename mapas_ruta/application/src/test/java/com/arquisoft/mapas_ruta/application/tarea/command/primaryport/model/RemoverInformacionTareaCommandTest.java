package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class RemoverInformacionTareaCommandTest {

    @Test
    void debeCrear_cuandoTareaValida() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverInformacionTareaCommand.crear(tarea);

        // Assert
        assertThat(command.tarea()).isEqualTo(tarea);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoTareaNula() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverInformacionTareaCommand.crear(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactly(tuple(MapasRutaFields.Tarea.TAREA,
                                        MapasRutaCodes.Tarea.TAREA_REQUERIDA)));
    }
}
