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

class ModificarInformacionTareaCommandTest {

    @Test
    void debeCrearConDescripcionRecortada_cuandoDatosValidos() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = ModificarInformacionTareaCommand.crear(tarea, "  Redactar capitulo ", asesor.toString());

        // Assert
        assertThat(command.tarea()).isEqualTo(tarea);
        assertThat(command.descripcion()).isEqualTo("Redactar capitulo");
        assertThat(command.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoTareaNulaDescripcionBlancaYAsesorBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> ModificarInformacionTareaCommand.crear(null, "  ", " "))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        tuple(MapasRutaFields.Tarea.TAREA, MapasRutaCodes.Tarea.TAREA_REQUERIDA),
                                        tuple(MapasRutaFields.Tarea.DESCRIPCION,
                                                MapasRutaCodes.Tarea.DESCRIPCION_REQUERIDA),
                                        tuple(MapasRutaFields.Tarea.ASESOR,
                                                MapasRutaCodes.Tarea.ASESOR_REQUERIDO)));
    }

    @Test
    void debeAcumularErrores_cuandoDescripcionSuperaMaximoYAsesorNoEsUuid() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var descripcion = "a".repeat(201);

        // Act & Assert
        assertThatThrownBy(() -> ModificarInformacionTareaCommand.crear(tarea, descripcion, "no-uuid"))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        tuple(MapasRutaFields.Tarea.DESCRIPCION,
                                                MapasRutaCodes.Tarea.DESCRIPCION_DEMASIADO_LARGA),
                                        tuple(MapasRutaFields.Tarea.ASESOR,
                                                MapasRutaCodes.Tarea.ASESOR_INVALIDO)));
    }
}
