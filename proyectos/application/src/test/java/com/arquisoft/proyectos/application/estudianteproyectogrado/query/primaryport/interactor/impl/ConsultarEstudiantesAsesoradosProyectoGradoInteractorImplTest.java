package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.EstudianteAsesoradoProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarEstudiantesAsesoradosProyectoGradoQuery;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteAsesoradoProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase.ConsultarEstudiantesAsesoradosProyectoGradoUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstudiantesAsesoradosProyectoGradoInteractorImplTest {

    @Mock
    private ConsultarEstudiantesAsesoradosProyectoGradoUseCase useCase;

    @InjectMocks
    private ConsultarEstudiantesAsesoradosProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCaseConCriteria_cuandoQueryValida() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var query = ConsultarEstudiantesAsesoradosProyectoGradoQuery.crear(proyectoGrado, asesor);
        var criteria = new EstudianteAsesoradoProyectoGradoCriteria(proyectoGrado, asesor);
        var esperado = List.of(new EstudianteAsesoradoProyectoGradoReadModel(
                UtilUUID.generarNuevoUUID(), "2020", "Ana", "ana@soyuco.edu.co"));
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
