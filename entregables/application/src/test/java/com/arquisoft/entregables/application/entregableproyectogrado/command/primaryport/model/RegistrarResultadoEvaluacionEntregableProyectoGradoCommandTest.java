package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarResultadoEvaluacionEntregableProyectoGradoCommandTest {

    @Test
    void debeCrearConUuidConvertidoYEstadoRecortado_cuandoLosDatosSonValidos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act
        var command = RegistrarResultadoEvaluacionEntregableProyectoGradoCommand.crear(
                entregable.toString(), " APROBADO ");

        // Assert
        assertThat(command.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(command.estadoEntregable()).isEqualTo("APROBADO");
    }

    @Test
    void debeAcumularErrores_cuandoElUuidEsInvalidoYElEstadoEstaEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> RegistrarResultadoEvaluacionEntregableProyectoGradoCommand.crear("no-uuid", " "))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> assertThat(
                        ex.getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(
                                EntregablesFields.EntregableProyectoGrado.ID,
                                EntregablesFields.EntregableProyectoGrado.ESTADO_ENTREGABLE));
    }

    @Test
    void debeRechazar_cuandoElEntregableEsElUuidDeCeros() {
        // Arrange
        var ceros = UtilUUID.obtenerPorDefectoComoTexto();

        // Act & Assert
        assertThatThrownBy(() -> RegistrarResultadoEvaluacionEntregableProyectoGradoCommand.crear(ceros, "APROBADO"))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> assertThat(
                        ex.getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(EntregablesFields.EntregableProyectoGrado.ID));
    }
}
