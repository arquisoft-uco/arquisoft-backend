package com.arquisoft.mapas_ruta.application.planreal.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealTareaCriteria;
import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.model.ConsultarPlanesRealesTareaEstudianteQuery;
import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealReadModel;
import com.arquisoft.mapas_ruta.application.planreal.query.usecase.ConsultarPlanesRealesTareaEstudianteUseCase;
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
class ConsultarPlanesRealesTareaEstudianteInteractorImplTest {

    @Mock
    private ConsultarPlanesRealesTareaEstudianteUseCase useCase;

    @InjectMocks
    private ConsultarPlanesRealesTareaEstudianteInteractorImpl interactor;

    @Test
    void debeConvertirQueryYDelegar_cuandoEjecuta() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var query = ConsultarPlanesRealesTareaEstudianteQuery.crear(tarea,
                ConsultaCriteriaQuery.crear(1, 5, List.of(), null));
        var esperado = PaginatedResult.<PlanRealReadModel>of(List.of(), 1, 5, 0);
        when(useCase.ejecutar(any(PlanRealTareaCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(PlanRealTareaCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getTarea()).isEqualTo(tarea);
        assertThat(captor.getValue().getPagina()).isEqualTo(1);
        assertThat(captor.getValue().getTamanio()).isEqualTo(5);
    }
}
