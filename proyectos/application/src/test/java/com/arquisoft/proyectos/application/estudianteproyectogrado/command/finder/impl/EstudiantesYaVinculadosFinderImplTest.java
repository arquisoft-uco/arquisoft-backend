package com.arquisoft.proyectos.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
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
class EstudiantesYaVinculadosFinderImplTest {

    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @InjectMocks
    private EstudiantesYaVinculadosFinderImpl finder;

    @Test
    void debeDevolverLosVinculadosDelPuerto_cuandoAlgunoYaEstaVinculado() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var vinculado = UtilUUID.generarNuevoUUID();
        var entrada = AgregacionEstudiantesProyectoGradoDomain.crear(EstudianteProyectoGradoDomain.crear(
                proyectoGrado, List.of(vinculado, UtilUUID.generarNuevoUUID())), UtilUUID.generarNuevoUUID());
        when(estudianteProyectoGradoOutputPort.obtenerVinculados(proyectoGrado, entrada.getEstudiantes()))
                .thenReturn(List.of(vinculado));

        // Act
        var resultado = finder.obtener(entrada);

        // Assert
        assertThat(resultado).containsExactly(vinculado);
    }
}
