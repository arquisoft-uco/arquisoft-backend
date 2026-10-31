package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoDetalleCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionArtefactoQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.RolConsultaVersionArtefacto;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.application.versionartefacto.query.usecase.ConsultarVersionArtefactoUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarVersionArtefactoInteractorImplTest {

    @Mock
    private ConsultarVersionArtefactoUseCase consultarVersionArtefactoUseCase;

    @InjectMocks
    private ConsultarVersionArtefactoInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCase_yRetornarSuResultado() {
        // Arrange
        var query = ConsultarVersionArtefactoQuery.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                List.of(RolConsultaVersionArtefacto.ESTUDIANTE));
        var esperado = mock(VersionArtefactoReadModel.class);
        when(consultarVersionArtefactoUseCase.ejecutar(any(VersionArtefactoDetalleCriteria.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarVersionArtefactoUseCase).ejecutar(
                new VersionArtefactoDetalleCriteria(query.versionArtefacto(), query.actor(), false, true));
    }
}
