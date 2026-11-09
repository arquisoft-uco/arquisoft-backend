package com.arquisoft.proyectos.application.entregableproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntregableProyectoGradoPorIdFinderImplTest {

    @Mock
    private EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @InjectMocks
    private EntregableProyectoGradoPorIdFinderImpl finder;

    @Test
    void debeMapearAlDominio_cuandoElEntregableExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entity = new EntregableProyectoGradoEntity(id, UtilUUID.generarNuevoUUID(), 1, "CERRADO",
                Instant.parse("2026-11-01T10:00:00Z"));
        when(entregableProyectoGradoOutputPort.obtenerPorId(id)).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getEstadoEntregable()).isEqualTo(EstadoEntregable.CERRADO);
    }

    @Test
    void debeRetornarVacio_cuandoElEntregableNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(entregableProyectoGradoOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isSameAs(EntregableProyectoGradoDomain.VACIO);
    }
}
