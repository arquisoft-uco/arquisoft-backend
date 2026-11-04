package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.EstudianteArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarEstudiantesArtefactoQuery;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.EstudianteArtefactoReadModel;
import com.arquisoft.artefactos.application.estudianteartefacto.query.usecase.ConsultarEstudiantesArtefactoUseCase;
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
class ConsultarEstudiantesArtefactoInteractorImplTest {

    @Mock
    private ConsultarEstudiantesArtefactoUseCase consultarEstudiantesArtefactoUseCase;

    @InjectMocks
    private ConsultarEstudiantesArtefactoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElCriteria_cuandoEjecuta() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var query = ConsultarEstudiantesArtefactoQuery.crear(artefacto);
        var criteria = new EstudianteArtefactoCriteria(artefacto);
        var esperado = List.<EstudianteArtefactoReadModel>of();
        when(consultarEstudiantesArtefactoUseCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarEstudiantesArtefactoUseCase).ejecutar(criteria);
    }
}
