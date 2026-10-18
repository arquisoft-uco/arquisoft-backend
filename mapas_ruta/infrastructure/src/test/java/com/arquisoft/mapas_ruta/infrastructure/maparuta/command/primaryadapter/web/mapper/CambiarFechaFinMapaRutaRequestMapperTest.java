package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.web.dto.CambiarFechaFinMapaRutaRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarFechaFinMapaRutaRequestMapperTest {

    @Test
    void debeMapearDtoACommand() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var dto = new CambiarFechaFinMapaRutaRequestDTO("2026-12-15");

        // Act
        var command = CambiarFechaFinMapaRutaRequestMapper.toCommand(dto, mapaRuta, coordinador);

        // Assert
        assertThat(command.mapaRuta()).isEqualTo(mapaRuta);
        assertThat(command.coordinador()).isEqualTo(coordinador);
        assertThat(command.fechaFin()).isEqualTo(LocalDate.of(2026, 12, 15));
    }
}
