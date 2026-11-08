package com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoProyectoCriteria;
import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model.ConsultarPlanesEstimadosProyectoGradoAsesorQuery;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoProyectoReadModel;
import com.arquisoft.mapas_ruta.application.planestimado.query.usecase.ConsultarPlanesEstimadosProyectoGradoAsesorUseCase;
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
class ConsultarPlanesEstimadosProyectoGradoAsesorInteractorImplTest {

    @Mock
    private ConsultarPlanesEstimadosProyectoGradoAsesorUseCase useCase;

    @InjectMocks
    private ConsultarPlanesEstimadosProyectoGradoAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCase_conElCriteriaMapeado() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var query = ConsultarPlanesEstimadosProyectoGradoAsesorQuery.crear(proyectoGrado,
                ConsultaCriteriaQuery.crear(1, 5, List.of(), null));
        var esperado = PaginatedResult.<PlanEstimadoProyectoReadModel>of(List.of(), 1, 5, 0);
        when(useCase.ejecutar(any(PlanEstimadoProyectoCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(PlanEstimadoProyectoCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(captor.getValue().getPagina()).isEqualTo(1);
        assertThat(captor.getValue().getTamanio()).isEqualTo(5);
    }
}
