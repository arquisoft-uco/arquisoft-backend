package com.arquisoft.fichas.application.observacionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.RemoverObservacionItemCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverObservacionItemMapperTest {

    @Test
    void debeConservarLosIds_cuandoConvierteElCommandADomain() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var command = RemoverObservacionItemCommand.crear(observacionItem, asesorFicha);

        // Act
        var remocion = RemoverObservacionItemMapper.toDomain(command);

        // Assert
        assertThat(remocion.getObservacionItem()).isEqualTo(observacionItem);
        assertThat(remocion.getAsesorFicha()).isEqualTo(asesorFicha);
    }
}
