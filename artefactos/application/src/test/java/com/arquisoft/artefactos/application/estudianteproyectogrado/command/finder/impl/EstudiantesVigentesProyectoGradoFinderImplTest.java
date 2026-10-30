package com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantesVigentesProyectoGradoFinderImplTest {

    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @InjectMocks
    private EstudiantesVigentesProyectoGradoFinderImpl finder;

    @Test
    void debeDevolverLosEstudiantesVigentes_cuandoElProyectoTieneEstudiantes() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        when(estudianteProyectoGradoOutputPort.obtenerVigentesPorProyectoGrado(proyecto))
                .thenReturn(List.of(primero, segundo));

        // Act
        var resultado = finder.obtener(proyecto);

        // Assert
        assertThat(resultado).containsExactly(primero, segundo);
    }
}
