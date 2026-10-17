package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresProyectoGradoEstudianteQuery;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase.ConsultarAsesoresProyectoGradoEstudianteUseCase;
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
class ConsultarAsesoresProyectoGradoEstudianteInteractorImplTest {

    @Mock
    private ConsultarAsesoresProyectoGradoEstudianteUseCase useCase;

    @InjectMocks
    private ConsultarAsesoresProyectoGradoEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCaseConElCriteria_cuandoEjecuta() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarAsesoresProyectoGradoEstudianteQuery.crear(proyectoGrado, estudiante);
        var criteria = new AsesorProyectoGradoEstudianteCriteria(proyectoGrado, estudiante);
        var esperado = List.<AsesorProyectoGradoReadModel>of();
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
