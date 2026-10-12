package com.arquisoft.fichas.application.revisionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.RemoverRevisionItemCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverRevisionItemMapperTest {

    @Test
    void debeConservarLosIds_cuandoConvierteElCommandADomain() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var command = RemoverRevisionItemCommand.crear(revisionItem, asesorFicha);

        // Act
        var remocion = RemoverRevisionItemMapper.toDomain(command);

        // Assert
        assertThat(remocion.getRevisionItem()).isEqualTo(revisionItem);
        assertThat(remocion.getAsesorFicha()).isEqualTo(asesorFicha);
    }
}
