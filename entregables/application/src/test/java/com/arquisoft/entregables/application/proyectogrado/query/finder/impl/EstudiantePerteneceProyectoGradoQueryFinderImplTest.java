package com.arquisoft.entregables.application.proyectogrado.query.finder.impl;

import com.arquisoft.entregables.application.proyectogrado.query.finder.model.EstudianteProyectoGrado;
import com.arquisoft.entregables.application.proyectogrado.query.secondaryport.EstudianteProyectoGradoAccesoQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantePerteneceProyectoGradoQueryFinderImplTest {

    @Mock
    private EstudianteProyectoGradoAccesoQueryOutputPort outputPort;

    @InjectMocks
    private EstudiantePerteneceProyectoGradoQueryFinderImpl finder;

    @Test
    void debeRetornarLoQueResuelveElPuerto() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        when(outputPort.perteneceAlProyecto(estudiante, proyecto)).thenReturn(false);

        // Act
        var resultado = finder.obtener(new EstudianteProyectoGrado(estudiante, proyecto));

        // Assert
        assertThat(resultado).isFalse();
    }
}
