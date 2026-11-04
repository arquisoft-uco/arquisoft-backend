package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.EstudianteProyectoGradoCoordinadorCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarEstudiantesProyectoGradoCoordinadorQuery;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase.ConsultarEstudiantesProyectoGradoCoordinadorUseCase;
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
class ConsultarEstudiantesProyectoGradoCoordinadorInteractorImplTest {

    @Mock
    private ConsultarEstudiantesProyectoGradoCoordinadorUseCase useCase;

    @InjectMocks
    private ConsultarEstudiantesProyectoGradoCoordinadorInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCaseConCriteria_cuandoEjecuta() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var query = ConsultarEstudiantesProyectoGradoCoordinadorQuery.crear(proyectoGrado, coordinador);
        var criteria = new EstudianteProyectoGradoCoordinadorCriteria(proyectoGrado, coordinador);
        var esperado = List.of(new EstudianteProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), proyectoGrado,
                UtilUUID.generarNuevoUUID(), "2020", "Ana", "ana@soyuco.edu.co", true));
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
