package com.arquisoft.proyectos.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantesVinculadosContadorFinderImplTest {

    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @InjectMocks
    private EstudiantesVinculadosContadorFinderImpl finder;

    @Test
    void debeDevolverElConteoDelPuerto_cuandoSeConsultaPorProyecto() {
        // Arrange
        var proyectoGrado = UUID.randomUUID();
        when(estudianteProyectoGradoOutputPort.contarPorProyectoGrado(proyectoGrado)).thenReturn(2L);

        // Act
        var vinculados = finder.obtener(proyectoGrado);

        // Assert
        assertThat(vinculados).isEqualTo(2L);
    }
}
