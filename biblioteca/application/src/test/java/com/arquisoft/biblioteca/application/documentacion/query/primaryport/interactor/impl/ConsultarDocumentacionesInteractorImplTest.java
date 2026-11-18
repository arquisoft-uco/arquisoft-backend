package com.arquisoft.biblioteca.application.documentacion.query.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.ConsultarDocumentacionesQuery;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.RolConsultaDocumentacion;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.usecase.ConsultarDocumentacionesUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
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
class ConsultarDocumentacionesInteractorImplTest {

    @Mock
    private ConsultarDocumentacionesUseCase consultarDocumentacionesUseCase;

    @Captor
    private ArgumentCaptor<DocumentacionCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarDocumentacionesInteractorImpl interactor;

    @Test
    void debeConvertirQueryACriteriaYDelegar_cuandoEjecuta() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarDocumentacionesQuery.crear(List.of(RolConsultaDocumentacion.ESTUDIANTE), criterio);
        var esperado = PaginatedResult.<DocumentacionReadModel>of(List.of(), 0, 10, 0L);
        when(consultarDocumentacionesUseCase.ejecutar(any(DocumentacionCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarDocumentacionesUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().getRaiz()).isNotNull();
    }
}
