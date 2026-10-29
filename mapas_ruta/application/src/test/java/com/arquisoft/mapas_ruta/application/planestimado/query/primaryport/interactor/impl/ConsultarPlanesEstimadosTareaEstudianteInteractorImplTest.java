package com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoTareaCriteria;
import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model.ConsultarPlanesEstimadosTareaEstudianteQuery;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoReadModel;
import com.arquisoft.mapas_ruta.application.planestimado.query.usecase.ConsultarPlanesEstimadosTareaEstudianteUseCase;
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
class ConsultarPlanesEstimadosTareaEstudianteInteractorImplTest {

    @Mock
    private ConsultarPlanesEstimadosTareaEstudianteUseCase useCase;

    @InjectMocks
    private ConsultarPlanesEstimadosTareaEstudianteInteractorImpl interactor;

    @Test
    void debeConvertirQueryYDelegar_cuandoEjecuta() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var query = ConsultarPlanesEstimadosTareaEstudianteQuery.crear(tarea,
                ConsultaCriteriaQuery.crear(1, 5, List.of(), null));
        var esperado = PaginatedResult.<PlanEstimadoReadModel>of(List.of(), 1, 5, 0);
        when(useCase.ejecutar(any(PlanEstimadoTareaCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(PlanEstimadoTareaCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getTarea()).isEqualTo(tarea);
        assertThat(captor.getValue().getPagina()).isEqualTo(1);
        assertThat(captor.getValue().getTamanio()).isEqualTo(5);
    }
}
