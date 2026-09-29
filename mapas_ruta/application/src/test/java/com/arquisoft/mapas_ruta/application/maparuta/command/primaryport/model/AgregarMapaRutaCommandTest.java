package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class AgregarMapaRutaCommandTest {

    @Test
    void debeConvertirUuidYFechas_cuandoLosDatosSonValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var command = AgregarMapaRutaCommand.crear(
                proyectoGrado.toString(), coordinador, "2026-10-01", "2026-12-01");

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(command.coordinador()).isEqualTo(coordinador);
        assertThat(command.fechaInicio()).isEqualTo(LocalDate.of(2026, 10, 1));
        assertThat(command.fechaFin()).isEqualTo(LocalDate.of(2026, 12, 1));
    }

    @Test
    void debeAcumularErrores_cuandoProyectoNoEsUuidYFechasSonInvalidas() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> AgregarMapaRutaCommand.crear("no-uuid", coordinador, "2026-02-30", " "))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        tuple(MapasRutaFields.MapaRuta.PROYECTO_GRADO,
                                                MapasRutaCodes.MapaRuta.PROYECTO_GRADO_INVALIDO),
                                        tuple(MapasRutaFields.MapaRuta.FECHA_INICIO,
                                                MapasRutaCodes.MapaRuta.FECHA_INICIO_INVALIDA),
                                        tuple(MapasRutaFields.MapaRuta.FECHA_FIN,
                                                MapasRutaCodes.MapaRuta.FECHA_FIN_REQUERIDA)));
    }

    @Test
    void debeAcumularErrores_cuandoTodosLosCamposFaltan() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarMapaRutaCommand.crear(null, null, null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        MapasRutaCodes.MapaRuta.PROYECTO_GRADO_REQUERIDO,
                                        MapasRutaCodes.MapaRuta.COORDINADOR_REQUERIDO,
                                        MapasRutaCodes.MapaRuta.FECHA_INICIO_REQUERIDA,
                                        MapasRutaCodes.MapaRuta.FECHA_FIN_REQUERIDA));
    }

    @Test
    void debeRechazarFechaFinInvalida_cuandoTieneFormatoErroneo() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID().toString();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> AgregarMapaRutaCommand.crear(proyectoGrado, coordinador, "2026-10-01", "01/12/2026"))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactly(MapasRutaCodes.MapaRuta.FECHA_FIN_INVALIDA));
    }
}
