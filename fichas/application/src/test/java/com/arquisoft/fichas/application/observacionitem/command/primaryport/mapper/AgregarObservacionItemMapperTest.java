package com.arquisoft.fichas.application.observacionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.AgregarObservacionItemCommand;
import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AgregarObservacionItemMapperTest {

    @Test
    void debeMapearElCommandAAgregacionConEstadoPendiente_cuandoDatosValidos() {
        // Arrange — el estado inicial siempre es PENDIENTE, el Command no lo recibe
        var revisionItem = UUID.randomUUID();
        var asesorFicha = UUID.randomUUID();
        var command = AgregarObservacionItemCommand.crear(revisionItem, "Observación válida", asesorFicha);

        // Act
        var agregacion = AgregarObservacionItemMapper.toDomain(command);

        // Assert
        assertThat(agregacion.getRevisionItem()).isEqualTo(revisionItem);
        assertThat(agregacion.getObservacion()).isEqualTo("Observación válida");
        assertThat(agregacion.getAsesorFicha()).isEqualTo(asesorFicha);
        assertThat(agregacion.getObservacionItem().getEstadoObservacionRevision())
                .isEqualTo(EstadoObservacionRevision.PENDIENTE);
    }
}
