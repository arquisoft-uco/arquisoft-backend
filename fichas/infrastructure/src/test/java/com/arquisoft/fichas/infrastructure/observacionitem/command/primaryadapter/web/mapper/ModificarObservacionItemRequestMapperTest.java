package com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web.mapper;

import com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web.dto.ModificarObservacionItemRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarObservacionItemRequestMapperTest {

    @Test
    void debePasarLosTresValoresAlCommand_cuandoLaPeticionEsValida() {
        // Arrange
        var dto = new ModificarObservacionItemRequestDTO("  Observación válida  ");
        var observacionItemId = UtilUUID.generarNuevoUUID();
        var asesorFichaId = UtilUUID.generarNuevoUUID();

        // Act
        var command = ModificarObservacionItemRequestMapper.toCommand(dto, observacionItemId, asesorFichaId);

        // Assert
        assertThat(command.observacionItem()).isEqualTo(observacionItemId);
        assertThat(command.observacion()).isEqualTo("Observación válida");
        assertThat(command.asesorFicha()).isEqualTo(asesorFichaId);
    }
}
