package com.arquisoft.entregables.application.asesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.message.constant.EntregablesLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarAsesorCommandTest {

    @Test
    void debeCrearCommand_cuandoElPayloadEsValido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.now();

        // Act
        var command = AgregarAsesorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(command.id()).isEqualTo(id);
        assertThat(command.identificador()).isEqualTo("20161020123");
        assertThat(command.nombre()).isEqualTo("Ana Perez");
        assertThat(command.email()).isEqualTo("ana@uco.edu.co");
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoVariosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarAsesorCommand.crear(
                "no-es-un-uuid", " ", " ", " ", null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("codigoError")
                                .containsExactlyInAnyOrder(
                                        EntregablesCodes.Asesor.ID_REQUERIDO,
                                        EntregablesCodes.Asesor.IDENTIFICADOR_REQUERIDO,
                                        EntregablesCodes.Asesor.NOMBRE_REQUERIDO,
                                        EntregablesCodes.Asesor.EMAIL_REQUERIDO,
                                        EntregablesCodes.Asesor.OCURRIDO_EN_REQUERIDO));
    }

    @Test
    void debeAcumularErroresDeLongitud_cuandoLosTresCamposExcedenElMaximo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID().toString();
        var identificador = "1".repeat(EntregablesLimits.Asesor.IDENTIFICADOR_MAX + 1);
        var nombre = "n".repeat(EntregablesLimits.Asesor.NOMBRE_MAX + 1);
        var email = "e".repeat(EntregablesLimits.Asesor.EMAIL_MAX + 1);
        var ocurridoEn = Instant.now();

        // Act & Assert
        assertThatThrownBy(() -> AgregarAsesorCommand.crear(id, identificador, nombre, email, ocurridoEn))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(3);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(EntregablesFields.Asesor.IDENTIFICADOR);
                        assertThat(error.codigoError())
                                .isEqualTo(EntregablesCodes.Asesor.IDENTIFICADOR_LONGITUD_MAXIMA);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(EntregablesFields.Asesor.NOMBRE);
                        assertThat(error.codigoError())
                                .isEqualTo(EntregablesCodes.Asesor.NOMBRE_LONGITUD_MAXIMA);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(EntregablesFields.Asesor.EMAIL);
                        assertThat(error.codigoError())
                                .isEqualTo(EntregablesCodes.Asesor.EMAIL_LONGITUD_MAXIMA);
                    });
                });
    }

    @Test
    void debeCrearCommand_cuandoLosCamposTienenExactamenteElMaximo() {
        // Arrange
        var identificador = "1".repeat(EntregablesLimits.Asesor.IDENTIFICADOR_MAX);
        var nombre = "n".repeat(EntregablesLimits.Asesor.NOMBRE_MAX);
        var email = "e".repeat(EntregablesLimits.Asesor.EMAIL_MAX);

        // Act
        var command = AgregarAsesorCommand.crear(
                UtilUUID.generarNuevoUUID().toString(), identificador, nombre, email, Instant.now());

        // Assert
        assertThat(command.identificador()).isEqualTo(identificador);
        assertThat(command.nombre()).isEqualTo(nombre);
        assertThat(command.email()).isEqualTo(email);
    }

    @Test
    void debeRecortarEspacios_cuandoLosValoresTraenEspaciosAlBorde() {
        // Act
        var command = AgregarAsesorCommand.crear(UtilUUID.generarNuevoUUID().toString(), " 1036950123 ",
                "  Carlos Rios ", "  carlos@uco.edu.co ", Instant.now());

        // Assert
        assertThat(command.identificador()).isEqualTo("1036950123");
        assertThat(command.nombre()).isEqualTo("Carlos Rios");
        assertThat(command.email()).isEqualTo("carlos@uco.edu.co");
    }
}
