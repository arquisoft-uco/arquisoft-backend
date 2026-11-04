package com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.EstadoProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.model.ConsultarEstadosProyectoGradoEstudianteQuery;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.usecase.ConsultarEstadosProyectoGradoEstudianteUseCase;
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
class ConsultarEstadosProyectoGradoEstudianteInteractorImplTest {

    @Mock
    private ConsultarEstadosProyectoGradoEstudianteUseCase useCase;

    @InjectMocks
    private ConsultarEstadosProyectoGradoEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElCriteria_cuandoEjecuta() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarEstadosProyectoGradoEstudianteQuery.crear(proyectoGrado, estudiante);
        var criteria = new EstadoProyectoGradoEstudianteCriteria(proyectoGrado, estudiante);
        var esperado = List.<EstadoProyectoGradoReadModel>of();
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
