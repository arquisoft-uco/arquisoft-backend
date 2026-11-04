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

class CambiarEstadoItemTareaCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();

        // Act
        var command = CambiarEstadoItemTareaCommand.crear(tarea, "EN_PROGRESO");

        // Assert
        assertThat(command.tarea()).isEqualTo(tarea);
        assertThat(command.estadoItem()).isEqualTo("EN_PROGRESO");
    }

    @Test
    void debeAcumularErrores_cuandoTareaNulaYEstadoEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarEstadoItemTareaCommand.crear(null, "  "))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        tuple(MapasRutaFields.Tarea.TAREA,
                                                MapasRutaCodes.Tarea.TAREA_REQUERIDA),
                                        tuple(MapasRutaFields.Tarea.ESTADO_ITEM,
                                                MapasRutaCodes.Tarea.ESTADO_ITEM_REQUERIDO)));
    }
}
