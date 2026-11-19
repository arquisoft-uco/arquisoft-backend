package com.arquisoft.fichas.application.observacionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.CerrarObservacionItemCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CerrarObservacionItemMapperTest {

    @Test
    void debeConservarLosIds_cuandoConvierteElCommandAlObjetoDeAccion() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var command = CerrarObservacionItemCommand.crear(observacionItem, asesorFicha);

        // Act
        var cierre = CerrarObservacionItemMapper.toDomain(command);

        // Assert
        assertThat(cierre.getObservacionItem()).isEqualTo(observacionItem);
        assertThat(cierre.getAsesorFicha()).isEqualTo(asesorFicha);
    }
}
