package com.arquisoft.fichas.application.revisionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.AgregarRevisionItemCommand;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AgregarRevisionItemMapperTest {

    @Test
    void debeMapearElCommandAAgregacionConEstadoNueva_cuandoDatosValidos() {
        // Arrange — el estado inicial siempre es 'NUEVA', el Command no lo recibe
        UUID item = UUID.randomUUID();
        UUID asesorFicha = UUID.randomUUID();
        var command = AgregarRevisionItemCommand.crear(item, asesorFicha);

        // Act
        var agregacion = AgregarRevisionItemMapper.toDomain(command);

        // Assert
        assertThat(agregacion.getItem()).isEqualTo(item);
        assertThat(agregacion.getEstadoRevision()).isEqualTo(EstadoRevision.NUEVA);
        assertThat(agregacion.getAsesorFicha()).isEqualTo(asesorFicha);
    }
}
