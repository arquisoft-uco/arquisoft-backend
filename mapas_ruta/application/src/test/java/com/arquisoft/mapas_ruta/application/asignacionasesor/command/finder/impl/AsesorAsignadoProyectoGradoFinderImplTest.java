package com.arquisoft.mapas_ruta.application.asignacionasesor.command.finder.impl;

import com.arquisoft.mapas_ruta.application.asignacionasesor.command.finder.model.AsesorEnProyectoGrado;
import com.arquisoft.mapas_ruta.application.asignacionasesor.command.secondaryport.AsignacionAsesorOutputPort;
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
    private AsignacionAsesorOutputPort asignacionAsesorOutputPort;

    @InjectMocks
    private AsesorAsignadoProyectoGradoFinderImpl finder;

    @Test
    void debeRetornarElValorDelPuerto_cuandoConsultaAsignacionDelAsesor() {
        // Arrange
        var entrada = new AsesorEnProyectoGrado(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        when(asignacionAsesorOutputPort.estaAsignado(entrada.proyectoGrado(), entrada.asesor())).thenReturn(true);

        // Act & Assert
        assertThat(finder.obtener(entrada)).isTrue();
    }
}
