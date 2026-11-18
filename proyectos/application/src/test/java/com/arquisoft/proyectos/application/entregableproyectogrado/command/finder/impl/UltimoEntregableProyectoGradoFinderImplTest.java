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
class UltimoEntregableProyectoGradoFinderImplTest {

    @Mock
    private EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @InjectMocks
    private UltimoEntregableProyectoGradoFinderImpl finder;

    @Test
    void debeMapearAlDominio_cuandoElProyectoTieneEntregable() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var entity = new EntregableProyectoGradoEntity(UtilUUID.generarNuevoUUID(), proyectoGrado, 3, "CANCELADO",
                Instant.parse("2026-11-01T10:00:00Z"));
        when(entregableProyectoGradoOutputPort.obtenerUltimoPorProyectoGrado(proyectoGrado))
                .thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(proyectoGrado);

        // Assert
        assertThat(resultado.getId()).isEqualTo(entity.id());
        assertThat(resultado.getVersion()).isEqualTo(3);
        assertThat(resultado.getEstadoEntregable()).isEqualTo(EstadoEntregable.CANCELADO);
    }

    @Test
    void debeRetornarVacio_cuandoNoHayEntregable() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(entregableProyectoGradoOutputPort.obtenerUltimoPorProyectoGrado(proyectoGrado))
                .thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(proyectoGrado);

        // Assert
        assertThat(resultado).isSameAs(EntregableProyectoGradoDomain.VACIO);
    }
}
