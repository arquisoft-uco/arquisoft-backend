package com.arquisoft.fichas.application.observacionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.ReabrirObservacionItemCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReabrirObservacionItemMapperTest {

    @Test
    void debeConservarLosIds_cuandoConvierteElCommandAlObjetoDeAccion() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var command = ReabrirObservacionItemCommand.crear(observacionItem, asesorFicha);

        // Act
        var reapertura = ReabrirObservacionItemMapper.toDomain(command);

        // Assert
        assertThat(reapertura.getObservacionItem()).isEqualTo(observacionItem);
        assertThat(reapertura.getAsesorFicha()).isEqualTo(asesorFicha);
    }
}
