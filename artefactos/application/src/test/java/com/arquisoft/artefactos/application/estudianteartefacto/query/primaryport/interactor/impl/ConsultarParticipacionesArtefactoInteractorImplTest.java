package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.ParticipacionArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarParticipacionesArtefactoQuery;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;
import com.arquisoft.artefactos.application.estudianteartefacto.query.usecase.ConsultarParticipacionesArtefactoUseCase;
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
class ConsultarParticipacionesArtefactoInteractorImplTest {

    @Mock
    private ConsultarParticipacionesArtefactoUseCase consultarParticipacionesArtefactoUseCase;

    @InjectMocks
    private ConsultarParticipacionesArtefactoInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElCriteria_cuandoEjecuta() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarParticipacionesArtefactoQuery.crear(estudiante);
        var criteria = new ParticipacionArtefactoCriteria(estudiante);
        var esperado = List.<ParticipacionArtefactoReadModel>of();
        when(consultarParticipacionesArtefactoUseCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarParticipacionesArtefactoUseCase).ejecutar(criteria);
    }
}
