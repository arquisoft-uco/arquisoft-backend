package com.arquisoft.fichas.application.revisionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.EnviarCorreccionesRevisionItemCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnviarCorreccionesRevisionItemMapperTest {

    @Test
    void debeConservarLosIds_cuandoConvierteElCommandADomain() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var command = EnviarCorreccionesRevisionItemCommand.crear(revisionItem, estudiante);

        // Act
        var envio = EnviarCorreccionesRevisionItemMapper.toDomain(command);

        // Assert
        assertThat(envio.getRevisionItem()).isEqualTo(revisionItem);
        assertThat(envio.getEstudiante()).isEqualTo(estudiante);
    }
}
