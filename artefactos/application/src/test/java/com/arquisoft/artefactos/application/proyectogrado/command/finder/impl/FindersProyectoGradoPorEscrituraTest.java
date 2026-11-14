package com.arquisoft.artefactos.application.proyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindersProyectoGradoPorEscrituraTest {

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;

    @Test
    void debeMapearAlDomainConFechas_cuandoExistePorRevisionAsesor() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var revision = UtilUUID.generarNuevoUUID();
        var entregadoEn = Instant.parse("2026-11-01T10:00:00Z");
        when(proyectoGradoOutputPort.obtenerBloqueadoPorRevisionAsesor(revision))
                .thenReturn(Optional.of(new ProyectoGradoEntity(id, "Proyecto", entregadoEn, UtilFecha.VACIO, null)));

        // Act
        var proyecto = new ProyectoGradoBloqueadoPorRevisionAsesorFinderImpl(proyectoGradoOutputPort).obtener(revision);

        // Assert
        assertThat(proyecto.getId()).isEqualTo(id);
        assertThat(proyecto.getEntregadoEn()).isEqualTo(entregadoEn);
        assertThat(proyecto.estaCongelado()).isTrue();
    }

    @Test
    void debeDevolverVacio_cuandoNoExistePorRevisionAsesor() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        when(proyectoGradoOutputPort.obtenerBloqueadoPorRevisionAsesor(revision)).thenReturn(Optional.empty());

        // Act
        var proyecto = new ProyectoGradoBloqueadoPorRevisionAsesorFinderImpl(proyectoGradoOutputPort).obtener(revision);

        // Assert
        assertThat(proyecto.esVacio()).isTrue();
    }

    @Test
    void debeMapearAlDomain_cuandoExistePorObservacion() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var observacion = UtilUUID.generarNuevoUUID();
        when(proyectoGradoOutputPort.obtenerBloqueadoPorObservacion(observacion))
                .thenReturn(Optional.of(new ProyectoGradoEntity(id, "Proyecto", UtilFecha.VACIO, UtilFecha.VACIO, null)));

        // Act
        var proyecto = new ProyectoGradoBloqueadoPorObservacionFinderImpl(proyectoGradoOutputPort).obtener(observacion);

        // Assert
        assertThat(proyecto.getId()).isEqualTo(id);
        assertThat(proyecto.estaCongelado()).isFalse();
    }

    @Test
    void debeDevolverVacio_cuandoNoExistePorObservacion() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        when(proyectoGradoOutputPort.obtenerBloqueadoPorObservacion(observacion)).thenReturn(Optional.empty());

        // Act
        var proyecto = new ProyectoGradoBloqueadoPorObservacionFinderImpl(proyectoGradoOutputPort).obtener(observacion);

        // Assert
        assertThat(proyecto.esVacio()).isTrue();
    }

    @Test
    void debeMapearAlDomain_cuandoExistePorVersionArtefacto() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var finalizadoEn = Instant.parse("2026-11-02T10:00:00Z");
        when(proyectoGradoOutputPort.obtenerBloqueadoPorVersionArtefacto(version))
                .thenReturn(Optional.of(new ProyectoGradoEntity(id, "Proyecto", UtilFecha.VACIO, finalizadoEn, null)));

        // Act
        var proyecto = new ProyectoGradoBloqueadoPorVersionArtefactoFinderImpl(proyectoGradoOutputPort).obtener(version);

        // Assert
        assertThat(proyecto.getFinalizadoEn()).isEqualTo(finalizadoEn);
        assertThat(proyecto.estaCongelado()).isTrue();
    }

    @Test
    void debeDevolverVacio_cuandoNoExistePorVersionArtefacto() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        when(proyectoGradoOutputPort.obtenerBloqueadoPorVersionArtefacto(version)).thenReturn(Optional.empty());

        // Act
        var proyecto = new ProyectoGradoBloqueadoPorVersionArtefactoFinderImpl(proyectoGradoOutputPort).obtener(version);

        // Assert
        assertThat(proyecto.esVacio()).isTrue();
    }
}
