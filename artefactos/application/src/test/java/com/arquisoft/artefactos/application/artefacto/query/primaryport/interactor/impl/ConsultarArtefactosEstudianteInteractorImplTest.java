package com.arquisoft.artefactos.application.artefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoEstudianteCriteria;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosEstudianteQuery;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoEstudianteReadModel;
import com.arquisoft.artefactos.application.artefacto.query.usecase.ConsultarArtefactosEstudianteUseCase;
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
class ConsultarArtefactosEstudianteInteractorImplTest {

    @Mock
    private ConsultarArtefactosEstudianteUseCase consultarArtefactosEstudianteUseCase;

    @Captor
    private ArgumentCaptor<ArtefactoEstudianteCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarArtefactosEstudianteInteractorImpl interactor;

    @Test
    void debeConvertirQueryACriteriaYDelegar_cuandoEjecuta() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarArtefactosEstudianteQuery.crear(estudiante, criterio);
        var resultadoEsperado = PaginatedResult.<ArtefactoEstudianteReadModel>of(List.of(), 0, 10, 0L);
        when(consultarArtefactosEstudianteUseCase.ejecutar(any(ArtefactoEstudianteCriteria.class)))
                .thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(consultarArtefactosEstudianteUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().getRaiz()).isEqualTo(
                NodoFiltro.predicado("estudianteId", FiltroOperador.ES, estudiante.toString()));
    }
}
