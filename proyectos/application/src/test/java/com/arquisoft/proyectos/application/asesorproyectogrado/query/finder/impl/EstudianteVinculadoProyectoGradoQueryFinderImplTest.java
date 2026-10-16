package com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.secondaryport.EstudianteProyectoGradoAccesoQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteVinculadoProyectoGradoQueryFinderImplTest {

    @Mock
    private EstudianteProyectoGradoAccesoQueryOutputPort outputPort;

    @InjectMocks
    private EstudianteVinculadoProyectoGradoQueryFinderImpl finder;

    @Test
    void debeDelegarEnElPuerto_cuandoObtiene() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        when(outputPort.existeVinculoVigente(proyectoGrado, estudiante)).thenReturn(true);

        // Act
        var resultado = finder.obtener(new AsesorProyectoGradoEstudianteCriteria(proyectoGrado, estudiante));

        // Assert
        assertThat(resultado).isTrue();
    }
}
