package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.CoordinadorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarCoordinadoresProyectoGradoEstudianteQuery;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.CoordinadorProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.usecase.ConsultarCoordinadoresProyectoGradoEstudianteUseCase;
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
class ConsultarCoordinadoresProyectoGradoEstudianteInteractorImplTest {

    @Mock
    private ConsultarCoordinadoresProyectoGradoEstudianteUseCase useCase;

    @InjectMocks
    private ConsultarCoordinadoresProyectoGradoEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConLaCriteria_cuandoSeEjecuta() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarCoordinadoresProyectoGradoEstudianteQuery.crear(estudiante);
        var criteria = new CoordinadorProyectoGradoEstudianteCriteria(estudiante);
        var esperado = List.<CoordinadorProyectoGradoReadModel>of();
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
