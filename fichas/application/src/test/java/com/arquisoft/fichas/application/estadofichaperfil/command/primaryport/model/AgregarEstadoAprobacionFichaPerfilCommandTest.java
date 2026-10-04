package com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarEstadoAprobacionFichaPerfilCommandTest {

    @Test
    void debeCrearCommand_cuandoLaEntradaEsValida() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var coordinador = UUID.randomUUID();

        // Act
        var command = AgregarEstadoAprobacionFichaPerfilCommand.crear(fichaPerfil.toString(), false, coordinador);

        // Assert
        assertThat(command.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(command.acepta()).isFalse();
        assertThat(command.coordinador()).isEqualTo(coordinador);
    }

    @Test
    void debeAcumularLosTresErrores_cuandoFichaNoEsUuidYAceptaYCoordinadorSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarEstadoAprobacionFichaPerfilCommand.crear("no-es-un-uuid", null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(3);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(FichasFields.EstadoFichaPerfil.FICHA_PERFIL);
                        assertThat(error.codigoError())
                                .isEqualTo(FichasCodes.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(FichasFields.EstadoFichaPerfil.ACEPTA);
                        assertThat(error.codigoError()).isEqualTo(FichasCodes.EstadoFichaPerfil.ACEPTA_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(FichasFields.EstadoFichaPerfil.COORDINADOR);
                        assertThat(error.codigoError())
                                .isEqualTo(FichasCodes.EstadoFichaPerfil.COORDINADOR_ID_REQUERIDO);
                    });
                });
    }

    @Test
    void debeReportarUnSoloErrorDeFicha_cuandoLaFichaLlegaEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarEstadoAprobacionFichaPerfilCommand.crear(" ", true, UUID.randomUUID()))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(FichasCodes.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO)));
    }
}
