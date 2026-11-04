package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoEstudianteQuery;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.usecase.ConsultarProyectosGradoEstudianteUseCase;
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
class ConsultarProyectosGradoEstudianteInteractorImplTest {

    @Mock
    private ConsultarProyectosGradoEstudianteUseCase useCase;

    @InjectMocks
    private ConsultarProyectosGradoEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarConElCriteriaDelMapper_cuandoEjecuta() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarProyectosGradoEstudianteQuery.crear(estudiante);
        var criteria = new ProyectoGradoEstudianteCriteria(estudiante);
        var esperado = List.<ProyectoGradoReadModel>of();
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
