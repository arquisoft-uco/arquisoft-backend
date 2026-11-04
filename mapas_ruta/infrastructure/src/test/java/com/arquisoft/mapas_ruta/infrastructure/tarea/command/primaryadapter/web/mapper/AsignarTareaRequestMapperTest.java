package com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.dto.AsignarTareaRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsignarTareaRequestMapperTest {

    @Test
    void debeMapearDtoACommand() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var dto = new AsignarTareaRequestDTO("Redactar capitulo", asesor.toString());

        // Act
        var command = AsignarTareaRequestMapper.toCommand(dto, mapaRuta);

        // Assert
        assertThat(command.mapaRuta()).isEqualTo(mapaRuta);
        assertThat(command.descripcion()).isEqualTo("Redactar capitulo");
        assertThat(command.asesor()).isEqualTo(asesor);
    }
}
