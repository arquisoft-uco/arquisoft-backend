package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorActualProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresActualesProyectoGradoEstudianteQuery;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorActualProyectoGradoReadModel;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase.ConsultarAsesoresActualesProyectoGradoEstudianteUseCase;
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
class ConsultarAsesoresActualesProyectoGradoEstudianteInteractorImplTest {

    @Mock
    private ConsultarAsesoresActualesProyectoGradoEstudianteUseCase useCase;

    @InjectMocks
    private ConsultarAsesoresActualesProyectoGradoEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConLaCriteria_cuandoSeEjecuta() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarAsesoresActualesProyectoGradoEstudianteQuery.crear(estudiante);
        var criteria = new AsesorActualProyectoGradoEstudianteCriteria(estudiante);
        var esperado = List.<AsesorActualProyectoGradoReadModel>of();
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
