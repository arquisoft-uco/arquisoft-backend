package com.arquisoft.fichas.application.revisionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.ModificarRevisionItemCommand;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarRevisionItemMapperTest {

    @Test
    void debeMapearElCommandAModificacion_cuandoElEstadoEsValido() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID asesorFicha = UUID.randomUUID();
        var command = ModificarRevisionItemCommand.crear(item, "VISUALIZADA", asesorFicha);

        // Act
        var modificacion = ModificarRevisionItemMapper.toDomain(command);

        // Assert
        assertThat(modificacion.getItem()).isEqualTo(item);
        assertThat(modificacion.getEstadoRevision()).isEqualTo(EstadoRevision.VISUALIZADA);
        assertThat(modificacion.getAsesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debePropagarLaExcepcionDelObjetoDeAccion_cuandoElEstadoNoPerteneceAlCatalogo() {
        // Arrange — el Command lo deja pasar; ModificacionRevisionItemDomain.crear es quien lo rechaza
        var command = ModificarRevisionItemCommand.crear(
                UUID.randomUUID(), "ESTADO_DESCONOCIDO", UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> ModificarRevisionItemMapper.toDomain(command))
                .isInstanceOf(DomainValidationException.class);
    }
}
