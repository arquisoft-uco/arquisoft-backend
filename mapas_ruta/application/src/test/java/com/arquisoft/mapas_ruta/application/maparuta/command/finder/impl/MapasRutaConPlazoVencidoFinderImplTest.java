package com.arquisoft.mapas_ruta.application.maparuta.command.finder.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.MapaRutaOutputPort;
import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.entity.MapaRutaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapasRutaConPlazoVencidoFinderImplTest {

    @Mock
    private MapaRutaOutputPort mapaRutaOutputPort;

    @InjectMocks
    private MapasRutaConPlazoVencidoFinderImpl finder;

    @Test
    void debeConsultarSoloProyectosEnProcesoYMapearADominio_cuandoHayVencidos() {
        // Arrange
        var hoy = LocalDate.of(2026, 11, 19);
        var entity = new MapaRutaEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 11, 1));
        when(mapaRutaOutputPort.obtenerConPlazoVencido(hoy, "EN_PROCESO")).thenReturn(List.of(entity));

        // Act
        var resultado = finder.obtener(hoy);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(entity.id());
        assertThat(resultado.get(0).getProyectoGrado()).isEqualTo(entity.proyectoGrado());
        assertThat(resultado.get(0).getFechaFin()).isEqualTo(entity.fechaFin());
        verify(mapaRutaOutputPort).obtenerConPlazoVencido(hoy, "EN_PROCESO");
    }
}
