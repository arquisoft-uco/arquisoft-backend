package com.arquisoft.proyectos.application.estadoproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.EstadoProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.entity.EstadoProyectoGradoEntity;
import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
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
class EstadoActualProyectoGradoFinderImplTest {

    @Mock
    private EstadoProyectoGradoOutputPort estadoProyectoGradoOutputPort;

    @InjectMocks
    private EstadoActualProyectoGradoFinderImpl finder;

    @Test
    void debeReconstruirElEstadoVigente_cuandoElProyectoTieneEstado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var fecha = Instant.parse("2026-10-01T10:00:00Z");
        when(estadoProyectoGradoOutputPort.obtenerEstadoActual(proyectoGrado)).thenReturn(Optional.of(
                new EstadoProyectoGradoEntity(id, proyectoGrado, EstadoProyecto.FINALIZADO.getId(), fecha)));

        // Act
        var estado = finder.obtener(proyectoGrado);

        // Assert
        assertThat(estado.esVacio()).isFalse();
        assertThat(estado.getId()).isEqualTo(id);
        assertThat(estado.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(estado.getEstadoProyecto()).isEqualTo(EstadoProyecto.FINALIZADO);
        assertThat(estado.getFechaActualizacion()).isEqualTo(fecha);
    }

    @Test
    void debeDevolverVacio_cuandoElProyectoNoTieneEstado() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(estadoProyectoGradoOutputPort.obtenerEstadoActual(proyectoGrado)).thenReturn(Optional.empty());

        // Act
        var estado = finder.obtener(proyectoGrado);

        // Assert
        assertThat(estado).isSameAs(EstadoProyectoGradoDomain.VACIO);
    }
}
