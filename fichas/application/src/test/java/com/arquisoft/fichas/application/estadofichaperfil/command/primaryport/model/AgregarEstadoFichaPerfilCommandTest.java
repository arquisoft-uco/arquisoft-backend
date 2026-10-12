package com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarEstadoFichaPerfilCommandTest {

    @Test
    void debeCrearCommand_cuandoLaEntradaEsValida() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var asesorFicha = UUID.randomUUID();

        // Act
        var command = AgregarEstadoFichaPerfilCommand.crear(fichaPerfil, "DESCARTADA", asesorFicha);

        // Assert
        assertThat(command.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(command.estadoFicha()).isEqualTo("DESCARTADA");
        assertThat(command.asesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debeAcumularLosTresErrores_cuandoFichaYAsesorSonNulosYElEstadoEstaEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarEstadoFichaPerfilCommand.crear(null, "  ", null))
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
                        assertThat(error.campo()).isEqualTo(FichasFields.EstadoFichaPerfil.ESTADO_FICHA);
                        assertThat(error.codigoError())
                                .isEqualTo(FichasCodes.EstadoFichaPerfil.ESTADO_FICHA_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(FichasFields.EstadoFichaPerfil.ASESOR_FICHA);
                        assertThat(error.codigoError())
                                .isEqualTo(FichasCodes.EstadoFichaPerfil.ASESOR_FICHA_ID_REQUERIDO);
                    });
                });
    }
}
