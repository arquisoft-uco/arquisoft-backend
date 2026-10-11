package com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsesoresAsignadosContadorFinderImplTest {

    @Mock
    private AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @InjectMocks
    private AsesoresAsignadosContadorFinderImpl finder;

    @Test
    void debePasarElEstadoAsignadoAlPuerto_cuandoSeCuentanLosAsesoresDelProyecto() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(asesorProyectoGradoOutputPort.contarPorProyectoGradoConEstadoVigente(
                proyectoGrado, EstadoAsesor.ASIGNADO.getId())).thenReturn(2L);

        // Act
        var asignados = finder.obtener(proyectoGrado);

        // Assert
        assertThat(asignados).isEqualTo(2L);
    }
}
