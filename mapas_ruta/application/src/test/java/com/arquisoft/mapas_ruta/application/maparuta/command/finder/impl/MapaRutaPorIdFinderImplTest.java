package com.arquisoft.mapas_ruta.application.maparuta.command.finder.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.MapaRutaOutputPort;
import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.entity.MapaRutaEntity;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapaRutaPorIdFinderImplTest {

    @Mock
    private MapaRutaOutputPort mapaRutaOutputPort;

    @InjectMocks
    private MapaRutaPorIdFinderImpl finder;

    @Test
    void debeDevolverDomain_cuandoExiste() {
        // Arrange
        var entity = new MapaRutaEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));
        when(mapaRutaOutputPort.obtenerPorId(entity.id())).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(entity.id());

        // Assert
        assertThat(resultado.getId()).isEqualTo(entity.id());
        assertThat(resultado.getProyectoGrado()).isEqualTo(entity.proyectoGrado());
        assertThat(resultado.getFechaInicio()).isEqualTo(entity.fechaInicio());
        assertThat(resultado.getFechaFin()).isEqualTo(entity.fechaFin());
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(mapaRutaOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isSameAs(MapaRutaDomain.VACIO);
    }
}
