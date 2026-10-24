package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.VersionRepositorioArtefactoCriteria;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model.ConsultarVersionesRepositorioArtefactoQuery;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase.ConsultarVersionesRepositorioArtefactoUseCase;
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
class ConsultarVersionesRepositorioArtefactoInteractorImplTest {

    @Mock
    private ConsultarVersionesRepositorioArtefactoUseCase useCase;

    @InjectMocks
    private ConsultarVersionesRepositorioArtefactoInteractorImpl interactor;

    @Test
    void debeConvertirYDelegarAlUseCase_cuandoSeEjecuta() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var query = ConsultarVersionesRepositorioArtefactoQuery.crear(repositorio);
        var esperado = List.<VersionRepositorioArtefactoReadModel>of();
        when(useCase.ejecutar(new VersionRepositorioArtefactoCriteria(repositorio))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(new VersionRepositorioArtefactoCriteria(repositorio));
    }
}
