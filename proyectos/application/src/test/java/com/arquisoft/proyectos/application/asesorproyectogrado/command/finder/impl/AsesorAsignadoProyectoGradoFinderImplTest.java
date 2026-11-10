package com.arquisoft.proyectos.application.asesorproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.proyectos.domain.proyectogrado.CambioEstadoProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsesorAsignadoProyectoGradoFinderImplTest {

    @Mock
    private AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @InjectMocks
    private AsesorAsignadoProyectoGradoFinderImpl finder;

    @Test
    void debeConsultarConElEstadoAsignado_cuandoSeObtiene() {
        // Arrange
        var cambio = CambioEstadoProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "FINALIZADO");
        when(asesorProyectoGradoOutputPort.existePorAsesorYProyectoGradoConEstadoVigente(
                cambio.getAsesor(), cambio.getProyectoGrado(), EstadoAsesor.ASIGNADO.getId())).thenReturn(true);

        // Act
        var resultado = finder.obtener(cambio);

        // Assert
        assertThat(resultado).isTrue();
    }
}
