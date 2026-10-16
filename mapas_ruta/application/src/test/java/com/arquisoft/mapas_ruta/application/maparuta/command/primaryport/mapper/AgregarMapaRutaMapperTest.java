package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.AgregarMapaRutaCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AgregarMapaRutaMapperTest {

    @Test
    void debeArmarLaAgregacionConMapaYCoordinador_cuandoElCommandoEsValido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var command = new AgregarMapaRutaCommand(
                proyectoGrado, coordinador, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));

        // Act
        var agregacion = AgregarMapaRutaMapper.toDomain(command);

        // Assert
        assertThat(agregacion.getCoordinador()).isEqualTo(coordinador);
        assertThat(agregacion.getMapaRuta().getId()).isNotNull();
        assertThat(agregacion.getMapaRuta().getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(agregacion.getMapaRuta().getFechaInicio()).isEqualTo(command.fechaInicio());
        assertThat(agregacion.getMapaRuta().getFechaFin()).isEqualTo(command.fechaFin());
    }
}
