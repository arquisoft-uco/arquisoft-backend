package com.arquisoft.fichas.application.observacionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.ModificarObservacionItemCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarObservacionItemMapperTest {

    @Test
    void debeCopiarLosTresCamposAlObjetoDeAccion_cuandoElCommandEsValido() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var command = ModificarObservacionItemCommand.crear(observacionItem, "Observación válida", asesorFicha);

        // Act
        var modificacion = ModificarObservacionItemMapper.toDomain(command);

        // Assert
        assertThat(modificacion.getObservacionItem()).isEqualTo(observacionItem);
        assertThat(modificacion.getObservacion()).isEqualTo("Observación válida");
        assertThat(modificacion.getAsesorFicha()).isEqualTo(asesorFicha);
    }
}
