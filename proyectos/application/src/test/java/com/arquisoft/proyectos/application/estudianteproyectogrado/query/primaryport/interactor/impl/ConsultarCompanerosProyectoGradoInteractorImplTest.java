package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.CompaneroProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarCompanerosProyectoGradoQuery;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.CompaneroProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase.ConsultarCompanerosProyectoGradoUseCase;
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
class ConsultarCompanerosProyectoGradoInteractorImplTest {

    @Mock
    private ConsultarCompanerosProyectoGradoUseCase useCase;

    @InjectMocks
    private ConsultarCompanerosProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCaseConCriteria_cuandoEjecuta() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarCompanerosProyectoGradoQuery.crear(proyectoGrado, estudiante);
        var criteria = new CompaneroProyectoGradoCriteria(proyectoGrado, estudiante);
        var esperado = List.of(new CompaneroProyectoGradoReadModel(estudiante, "2020", "Ana", "ana@soyuco.edu.co"));
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
