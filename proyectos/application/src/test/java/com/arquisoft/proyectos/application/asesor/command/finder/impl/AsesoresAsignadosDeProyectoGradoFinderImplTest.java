package com.arquisoft.proyectos.application.asesor.command.finder.impl;

import com.arquisoft.proyectos.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsesoresAsignadosDeProyectoGradoFinderImplTest {

    @Mock
    private AsesorOutputPort asesorOutputPort;

    @InjectMocks
    private AsesoresAsignadosDeProyectoGradoFinderImpl finder;

    @Test
    void debeMapearAsesoresAsignados_cuandoHayVinculosAsignados() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        when(asesorOutputPort.obtenerAsignadosPorProyectoGrado(proyectoGrado, EstadoAsesor.ASIGNADO.getId()))
                .thenReturn(List.of(new AsesorEntity(
                        id, "2015", "Mario Peña", "mario.pena@uco.edu.co", Instant.now(), UtilFecha.VACIO)));

        // Act
        var asesores = finder.obtener(proyectoGrado);

        // Assert
        assertThat(asesores).singleElement().satisfies(asesor -> {
            assertThat(asesor.getId()).isEqualTo(id);
            assertThat(asesor.getNombre()).isEqualTo("Mario Peña");
            assertThat(asesor.getEmail()).isEqualTo("mario.pena@uco.edu.co");
        });
    }

    @Test
    void debeDevolverListaVacia_cuandoNoHayAsesorAsignado() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(asesorOutputPort.obtenerAsignadosPorProyectoGrado(proyectoGrado, EstadoAsesor.ASIGNADO.getId()))
                .thenReturn(List.of());

        // Act
        var asesores = finder.obtener(proyectoGrado);

        // Assert
        assertThat(asesores).isEmpty();
    }
}
