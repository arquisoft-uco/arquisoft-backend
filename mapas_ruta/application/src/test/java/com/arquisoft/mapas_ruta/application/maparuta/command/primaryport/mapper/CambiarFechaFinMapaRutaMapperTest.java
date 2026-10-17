package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.CambiarFechaFinMapaRutaCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarFechaFinMapaRutaMapperTest {

    @Test
    void debeMapearADomain() {
        // Arrange
        var command = new CambiarFechaFinMapaRutaCommand(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), LocalDate.of(2026, 12, 15));

        // Act
        var cambio = CambiarFechaFinMapaRutaMapper.toDomain(command);

        // Assert
        assertThat(cambio.getMapaRuta()).isEqualTo(command.mapaRuta());
        assertThat(cambio.getCoordinador()).isEqualTo(command.coordinador());
        assertThat(cambio.getFechaFin()).isEqualTo(command.fechaFin());
    }
}
