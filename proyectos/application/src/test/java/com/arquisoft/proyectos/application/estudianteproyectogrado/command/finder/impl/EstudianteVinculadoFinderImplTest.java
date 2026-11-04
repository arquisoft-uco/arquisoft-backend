package com.arquisoft.proyectos.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.DestitucionEstudianteProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteVinculadoFinderImplTest {

    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @InjectMocks
    private EstudianteVinculadoFinderImpl finder;

    @Test
    void debeDevolverLoQueRespondeElPuerto_cuandoSeConsultaElVinculo() {
        // Arrange
        var destitucion = DestitucionEstudianteProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        when(estudianteProyectoGradoOutputPort.existeVinculo(
                destitucion.getProyectoGrado(), destitucion.getEstudiante())).thenReturn(true);

        // Act
        var vinculado = finder.obtener(destitucion);

        // Assert
        assertThat(vinculado).isTrue();
    }
}
