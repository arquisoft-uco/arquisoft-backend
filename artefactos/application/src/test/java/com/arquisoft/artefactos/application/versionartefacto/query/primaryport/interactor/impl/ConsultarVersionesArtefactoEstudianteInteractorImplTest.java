package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoEstudianteCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionesArtefactoEstudianteQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.application.versionartefacto.query.usecase.ConsultarVersionesArtefactoEstudianteUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarVersionesArtefactoEstudianteInteractorImplTest {

    @Mock
    private ConsultarVersionesArtefactoEstudianteUseCase consultarVersionesArtefactoEstudianteUseCase;

    @Captor
    private ArgumentCaptor<VersionArtefactoEstudianteCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarVersionesArtefactoEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCaseConElCriteriaDelMapper_yRetornarSuResultado() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarVersionesArtefactoEstudianteQuery.crear(estudiante, criterio);
        var resultadoEsperado = PaginatedResult.<VersionArtefactoReadModel>of(List.of(), 0, 10, 0L);
        when(consultarVersionesArtefactoEstudianteUseCase.ejecutar(any(VersionArtefactoEstudianteCriteria.class)))
                .thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(consultarVersionesArtefactoEstudianteUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().getRaiz()).isEqualTo(
                NodoFiltro.predicado("estudianteId", FiltroOperador.ES, estudiante.toString()));
    }
}
