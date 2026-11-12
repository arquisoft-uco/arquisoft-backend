package com.arquisoft.entregables.application.artefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarVersionCerradaArtefactoCommandTest {

    @Test
    void debeCrear_cuandoLosDatosDelEventoSonValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var fecha = Instant.now();

        // Act
        var command = RegistrarVersionCerradaArtefactoCommand.crear(
                artefacto.toString(), UtilUUID.generarNuevoUUID().toString(), "Documento",
                new RegistrarVersionCerradaArtefactoCommand.DatosVersion(
                        UtilUUID.generarNuevoUUID().toString(), 2, "pendientes/a.pdf", fecha),
                new RegistrarVersionCerradaArtefactoCommand.DatosRepositorio(
                        UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID().toString(), "Repo"));

        // Assert
        assertThat(command.artefacto()).isEqualTo(artefacto);
        assertThat(command.numeroVersion()).isEqualTo(2);
        assertThat(command.uriArchivo()).isEqualTo("pendientes/a.pdf");
        assertThat(command.fechaVersion()).isEqualTo(fecha);
        assertThat(command.nombreRepositorio()).isEqualTo("Repo");
    }

    @Test
    void debeAcumularErroresDeEntrada_cuandoElPayloadVieneIncompleto() {
        // Arrange
        var version = new RegistrarVersionCerradaArtefactoCommand.DatosVersion("no-uuid", 0, " ", null);
        var repositorio = new RegistrarVersionCerradaArtefactoCommand.DatosRepositorio(null, "x", "");

        // Act & Assert
        assertThatThrownBy(() -> RegistrarVersionCerradaArtefactoCommand.crear(null, "x", " ", version, repositorio))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    var resultado = ex.getValidationResult();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.Artefacto.ID)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.Artefacto.PROYECTO_GRADO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.Artefacto.NOMBRE_DOCUMENTO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.Artefacto.VERSION_ARTEFACTO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.Artefacto.NUMERO_VERSION)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.Artefacto.CLAVE_ORIGEN)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.Artefacto.FECHA_VERSION)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(
                            EntregablesFields.Artefacto.VERSION_REPOSITORIO_ARTEFACTO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.Artefacto.REPOSITORIO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.Artefacto.NOMBRE_REPOSITORIO)).isTrue();
                });
    }
}
