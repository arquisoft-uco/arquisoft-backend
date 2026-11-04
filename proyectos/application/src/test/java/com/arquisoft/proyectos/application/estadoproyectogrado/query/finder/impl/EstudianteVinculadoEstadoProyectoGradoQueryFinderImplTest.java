package com.arquisoft.proyectos.application.estadoproyectogrado.query.finder.impl;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.EstadoProyectoGradoEstudianteCriteria;
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
class EstudianteVinculadoEstadoProyectoGradoQueryFinderImplTest {

    @Mock
    private EstudianteProyectoGradoAccesoQueryOutputPort outputPort;

    @InjectMocks
    private EstudianteVinculadoEstadoProyectoGradoQueryFinderImpl finder;

    @Test
    void debeDelegarEnElPuertoDeAcceso_cuandoObtiene() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        when(outputPort.existeVinculoVigente(proyectoGrado, estudiante)).thenReturn(true);

        // Act
        var resultado = finder.obtener(new EstadoProyectoGradoEstudianteCriteria(proyectoGrado, estudiante));

        // Assert
        assertThat(resultado).isTrue();
    }
}
