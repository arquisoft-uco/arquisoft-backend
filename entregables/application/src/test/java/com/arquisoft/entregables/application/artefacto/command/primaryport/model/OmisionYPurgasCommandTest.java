package com.arquisoft.entregables.application.artefacto.command.primaryport.model;

import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model.PurgarPaquetesProyectoGradoCommand;
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
        var ocurridoEn = Instant.now();

        // Act
        var omitir = OmitirArtefactoCommand.crear(artefacto.toString(), ocurridoEn);
        var reactivar = ReactivarArtefactoCommand.crear(artefacto.toString(), ocurridoEn);

        // Assert
        assertThat(omitir.artefacto()).isEqualTo(artefacto);
        assertThat(omitir.ocurridoEn()).isEqualTo(ocurridoEn);
        assertThat(reactivar.artefacto()).isEqualTo(artefacto);
        assertThat(reactivar.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeRechazarConErroresDeEntrada_cuandoFaltanDatosEnOmitirYReactivar() {
        // Act & Assert
        assertThatThrownBy(() -> OmitirArtefactoCommand.crear("x", null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    assertThat(ex.getValidationResult().tieneErroresDeCampo(EntregablesFields.Artefacto.ID)).isTrue();
                    assertThat(ex.getValidationResult()
                            .tieneErroresDeCampo(EntregablesFields.Artefacto.OCURRIDO_EN)).isTrue();
                });
        assertThatThrownBy(() -> ReactivarArtefactoCommand.crear(null, null))
                .isInstanceOf(ApplicationValidationException.class);
    }

    @Test
    void debeCrearORechazarPurgas_segunElProyectoSeaUnUuid() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var purgaArtefactos = PurgarArtefactosProyectoGradoCommand.crear(proyecto.toString());
        var purgaPaquetes = PurgarPaquetesProyectoGradoCommand.crear(proyecto.toString());

        // Assert
        assertThat(purgaArtefactos.proyectoGrado()).isEqualTo(proyecto);
        assertThat(purgaPaquetes.proyectoGrado()).isEqualTo(proyecto);
        assertThatThrownBy(() -> PurgarArtefactosProyectoGradoCommand.crear("no-uuid"))
                .isInstanceOf(ApplicationValidationException.class);
        assertThatThrownBy(() -> PurgarPaquetesProyectoGradoCommand.crear(null))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
