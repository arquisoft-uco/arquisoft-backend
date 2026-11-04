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
class MapaRutaDeTareaFinderImplTest {

    @Mock
    private MapaRutaOutputPort mapaRutaOutputPort;

    @InjectMocks
    private MapaRutaDeTareaFinderImpl finder;

    @Test
    void debeDevolverDomain_cuandoLaTareaTieneMapa() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var entity = new MapaRutaEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));
        when(mapaRutaOutputPort.obtenerPorTarea(tarea)).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(tarea);

        // Assert
        assertThat(resultado.getId()).isEqualTo(entity.id());
        assertThat(resultado.getFechaInicio()).isEqualTo(entity.fechaInicio());
        assertThat(resultado.getFechaFin()).isEqualTo(entity.fechaFin());
        assertThat(resultado.esVacio()).isFalse();
    }

    @Test
    void debeDevolverVacio_cuandoLaTareaNoTieneMapa() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        when(mapaRutaOutputPort.obtenerPorTarea(tarea)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(tarea);

        // Assert
        assertThat(resultado).isSameAs(MapaRutaDomain.VACIO);
    }
}
