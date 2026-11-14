package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarEstadoEntregableProyectoGradoCommandTest {

    @Test
    void debeCrearConElEstadoRecortado_cuandoLosDatosSonValidos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = CambiarEstadoEntregableProyectoGradoCommand.crear(entregable, "  CANCELADO ", asesor);

        // Assert
        assertThat(command.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(command.estadoEntregable()).isEqualTo("CANCELADO");
        assertThat(command.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanAsesorEntregableYEstado() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarEstadoEntregableProyectoGradoCommand.crear(null, "   ", null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("codigoError")
                                .containsExactlyInAnyOrder(
                                        EntregablesCodes.EntregableProyectoGrado.ID_REQUERIDO,
                                        EntregablesCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_REQUERIDO,
                                        EntregablesCodes.EntregableProyectoGrado.ASESOR_REQUERIDO));
    }
}
