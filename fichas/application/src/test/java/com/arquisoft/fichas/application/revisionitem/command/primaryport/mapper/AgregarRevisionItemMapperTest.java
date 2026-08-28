package com.arquisoft.fichas.application.revisionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.AgregarRevisionItemCommand;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarRevisionItemMapperTest {

    @Test
    void debeMapearElCommandAAgregacion_cuandoElEstadoEsValido() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID asesorFicha = UUID.randomUUID();
        var command = AgregarRevisionItemCommand.crear(item, "NUEVA", asesorFicha);

        // Act
        var agregacion = AgregarRevisionItemMapper.toDomain(command);

        // Assert
        assertThat(agregacion.getItem()).isEqualTo(item);
        assertThat(agregacion.getEstadoRevision()).isEqualTo(EstadoRevision.NUEVA);
        assertThat(agregacion.getAsesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debePropagarLaExcepcionDelAgregado_cuandoElEstadoNoPerteneceAlCatalogo() {
        // Arrange — el Command lo deja pasar; RevisionItemDomain.crear es quien lo rechaza
        var command = AgregarRevisionItemCommand.crear(
                UUID.randomUUID(), "ESTADO_DESCONOCIDO", UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> AgregarRevisionItemMapper.toDomain(command))
                .isInstanceOf(DomainValidationException.class);
    }
}
