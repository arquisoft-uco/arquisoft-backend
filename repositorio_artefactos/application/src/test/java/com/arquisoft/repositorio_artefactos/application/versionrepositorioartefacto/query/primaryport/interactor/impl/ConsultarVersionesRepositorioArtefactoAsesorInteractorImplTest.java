package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.VersionRepositorioArtefactoAsesorCriteria;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoAsesorReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase.ConsultarVersionesRepositorioArtefactoAsesorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarVersionesRepositorioArtefactoAsesorInteractorImplTest {

    @Mock
    private ConsultarVersionesRepositorioArtefactoAsesorUseCase useCase;

    @InjectMocks
    private ConsultarVersionesRepositorioArtefactoAsesorInteractorImpl interactor;

    @Test
    void debeConvertirQueryEnCriteriaYDelegar_cuandoEjecuta() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(1, 20, List.of(), null);
        var esperado = PaginatedResult.<VersionRepositorioArtefactoAsesorReadModel>of(List.of(), 1, 20, 0L);
        when(useCase.ejecutar(any(VersionRepositorioArtefactoAsesorCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        var captor = ArgumentCaptor.forClass(VersionRepositorioArtefactoAsesorCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getPagina()).isEqualTo(1);
        assertThat(captor.getValue().getTamanio()).isEqualTo(20);
        assertThat(resultado).isSameAs(esperado);
    }
}
