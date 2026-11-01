package com.arquisoft.artefactos.application.artefacto.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoSinAvanceCriteria;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosSinAvanceQuery;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoSinAvanceReadModel;
import com.arquisoft.artefactos.application.artefacto.query.usecase.ConsultarArtefactosSinAvanceUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
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
class ConsultarArtefactosSinAvanceInteractorImplTest {

    @Mock
    private ConsultarArtefactosSinAvanceUseCase consultarArtefactosSinAvanceUseCase;

    @Captor
    private ArgumentCaptor<ArtefactoSinAvanceCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarArtefactosSinAvanceInteractorImpl interactor;

    @Test
    void debeConvertirQueryACriteriaYDelegar_cuandoEjecuta() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarArtefactosSinAvanceQuery.crear(asesor, 30, criterio);
        var resultadoEsperado = PaginatedResult.<ArtefactoSinAvanceReadModel>of(List.of(), 0, 10, 0L);
        when(consultarArtefactosSinAvanceUseCase.ejecutar(any(ArtefactoSinAvanceCriteria.class)))
                .thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(consultarArtefactosSinAvanceUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(criteriaCaptor.getValue().getFechaLimite()).isNotNull();
    }
}
