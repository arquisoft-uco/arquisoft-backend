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

class AsignarTareaCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = AsignarTareaCommand.crear(mapaRuta, "Redactar capitulo", asesor.toString());

        // Assert
        assertThat(command.mapaRuta()).isEqualTo(mapaRuta);
        assertThat(command.descripcion()).isEqualTo("Redactar capitulo");
        assertThat(command.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoDescripcionYAsesorEstanEnBlanco() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> AsignarTareaCommand.crear(mapaRuta, "  ", " "))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        tuple(MapasRutaFields.Tarea.DESCRIPCION,
                                                MapasRutaCodes.Tarea.DESCRIPCION_REQUERIDA),
                                        tuple(MapasRutaFields.Tarea.ASESOR,
                                                MapasRutaCodes.Tarea.ASESOR_REQUERIDO)));
    }

    @Test
    void debeRechazar_cuandoAsesorNoEsUuid() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> AsignarTareaCommand.crear(mapaRuta, "Tarea", "no-uuid"))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactly(tuple(MapasRutaFields.Tarea.ASESOR,
                                        MapasRutaCodes.Tarea.ASESOR_INVALIDO)));
    }

    @Test
    void debeAcumularErrores_cuandoTodosLosCamposFaltan() {
        // Act & Assert
        assertThatThrownBy(() -> AsignarTareaCommand.crear(null, null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        MapasRutaCodes.Tarea.MAPA_RUTA_REQUERIDO,
                                        MapasRutaCodes.Tarea.DESCRIPCION_REQUERIDA,
                                        MapasRutaCodes.Tarea.ASESOR_REQUERIDO));
    }
}
