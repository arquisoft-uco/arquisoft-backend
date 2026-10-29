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

class RemoverAsesorCommandTest {

    @Test
    void debeCrearCommand_cuandoElPayloadEsValido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-23T10:00:00Z");

        // Act
        var command = RemoverAsesorCommand.crear(
                id.toString(), "1036950123", "Carlos Rios", "carlos@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(command.id()).isEqualTo(id);
        assertThat(command.identificador()).isEqualTo("1036950123");
        assertThat(command.nombre()).isEqualTo("Carlos Rios");
        assertThat(command.email()).isEqualTo("carlos@uco.edu.co");
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoElPayloadEsInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverAsesorCommand.crear("no-es-un-uuid", " ", " ", " ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .contains(EntregablesFields.Asesor.ID, EntregablesFields.Asesor.IDENTIFICADOR,
                                EntregablesFields.Asesor.NOMBRE, EntregablesFields.Asesor.EMAIL,
                                EntregablesFields.Asesor.OCURRIDO_EN));
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
        assertThatThrownBy(() -> RemoverAsesorCommand.crear(id, identificador, nombre, email, ocurridoEn))
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
        var command = RemoverAsesorCommand.crear(
                UtilUUID.generarNuevoUUID().toString(), identificador, nombre, email, Instant.now());

        // Assert
        assertThat(command.identificador()).isEqualTo(identificador);
        assertThat(command.nombre()).isEqualTo(nombre);
        assertThat(command.email()).isEqualTo(email);
    }

    @Test
    void debeRecortarEspacios_cuandoLosValoresTraenEspaciosAlBorde() {
        // Act
        var command = RemoverAsesorCommand.crear(UtilUUID.generarNuevoUUID().toString(), " 1036950123 ",
                "  Carlos Rios ", "  carlos@uco.edu.co ", Instant.now());

        // Assert
        assertThat(command.identificador()).isEqualTo("1036950123");
        assertThat(command.nombre()).isEqualTo("Carlos Rios");
        assertThat(command.email()).isEqualTo("carlos@uco.edu.co");
    }
}
