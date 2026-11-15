package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionesAsignadasAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionesAsignadasAsesorQuery;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsignadaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase.ConsultarEvaluacionesAsignadasAsesorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
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
class ConsultarEvaluacionesAsignadasAsesorInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesAsignadasAsesorUseCase useCase;

    @InjectMocks
    private ConsultarEvaluacionesAsignadasAsesorInteractorImpl interactor;

    @Test
    void debeMapearQueryACriteriaYDelegar_retornandoElMismoResultado() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesAsignadasAsesorQuery.crear(
                asesor, ConsultaCriteriaQuery.crear(2, 5, List.of(), null));
        var esperado = PaginatedResult.<EvaluacionAsignadaAsesorReadModel>of(List.of(), 2, 5, 0);
        when(useCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(EvaluacionesAsignadasAsesorCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
        assertThat(captor.getValue().getPagina()).isEqualTo(2);
        assertThat(captor.getValue().getTamanio()).isEqualTo(5);
    }
}
