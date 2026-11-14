package com.arquisoft.entregables.application.artefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OmisionYPurgasCommandTest {

    @Test
    void debeCrearOmitirYReactivar_cuandoLosDatosSonValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.now();

        // Act
        var omitir = OmitirArtefactoCommand.crear(artefacto.toString(), proyecto.toString(), ocurridoEn);
        var reactivar = ReactivarArtefactoCommand.crear(artefacto.toString(), proyecto.toString(), ocurridoEn);

        // Assert
        assertThat(omitir.artefacto()).isEqualTo(artefacto);
        assertThat(omitir.proyectoGrado()).isEqualTo(proyecto);
        assertThat(reactivar.proyectoGrado()).isEqualTo(proyecto);
        assertThat(omitir.ocurridoEn()).isEqualTo(ocurridoEn);
        assertThat(reactivar.artefacto()).isEqualTo(artefacto);
        assertThat(reactivar.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeRechazarConErroresDeEntrada_cuandoFaltanDatosEnOmitirYReactivar() {
        // Act & Assert
        assertThatThrownBy(() -> OmitirArtefactoCommand.crear("x", "y", null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    assertThat(ex.getValidationResult().tieneErroresDeCampo(EntregablesFields.Artefacto.ID)).isTrue();
                    assertThat(ex.getValidationResult()
                            .tieneErroresDeCampo(EntregablesFields.Artefacto.PROYECTO_GRADO)).isTrue();
                    assertThat(ex.getValidationResult()
                            .tieneErroresDeCampo(EntregablesFields.Artefacto.OCURRIDO_EN)).isTrue();
                });
        assertThatThrownBy(() -> ReactivarArtefactoCommand.crear(null, null, null))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
