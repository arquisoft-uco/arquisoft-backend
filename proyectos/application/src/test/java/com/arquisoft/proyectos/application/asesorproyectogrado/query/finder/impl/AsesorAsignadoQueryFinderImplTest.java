package com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.model.AsignacionAsesorConsultada;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.secondaryport.AsesorProyectoGradoAccesoQueryOutputPort;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsesorAsignadoQueryFinderImplTest {

    @Mock
    private AsesorProyectoGradoAccesoQueryOutputPort outputPort;

    @InjectMocks
    private AsesorAsignadoQueryFinderImpl finder;

    @Test
    void debeConsultarConEstadoAsignado_cuandoSeObtiene() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(outputPort.existeAsignacionVigente(asesor, proyectoGrado, EstadoAsesor.ASIGNADO.getId()))
                .thenReturn(true);

        // Act
        var resultado = finder.obtener(new AsignacionAsesorConsultada(asesor, proyectoGrado));

        // Assert
        assertThat(resultado).isTrue();
        verify(outputPort).existeAsignacionVigente(asesor, proyectoGrado, "ASIGNADO");
    }

    @Test
    void debeRetornarFalse_cuandoPuertoRetornaFalse() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(outputPort.existeAsignacionVigente(asesor, proyectoGrado, EstadoAsesor.ASIGNADO.getId()))
                .thenReturn(false);

        // Act
        var resultado = finder.obtener(new AsignacionAsesorConsultada(asesor, proyectoGrado));

        // Assert
        assertThat(resultado).isFalse();
    }
}
