package com.arquisoft.entregables.application.artefactoentregable.query.primaryport.interactor.impl;

import com.arquisoft.entregables.application.artefactoentregable.query.criteria.ArtefactoEntregableCriteria;
import com.arquisoft.entregables.application.artefactoentregable.query.primaryport.model.ConsultarArtefactosEntregableQuery;
import com.arquisoft.entregables.application.artefactoentregable.query.readmodel.ArtefactoEntregableReadModel;
import com.arquisoft.entregables.application.artefactoentregable.query.usecase.ConsultarArtefactosEntregableUseCase;
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
class ConsultarArtefactosEntregableInteractorImplTest {

    @Mock
    private ConsultarArtefactosEntregableUseCase useCase;

    @InjectMocks
    private ConsultarArtefactosEntregableInteractorImpl interactor;

    @Test
    void debeConvertirLaQueryEnCriteria_yDelegarEnElUseCase() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var esperado = List.<ArtefactoEntregableReadModel>of();
        var asesor = UtilUUID.generarNuevoUUID();
        var criteria = new ArtefactoEntregableCriteria(entregable, asesor);
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(ConsultarArtefactosEntregableQuery.crear(entregable, asesor));

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
