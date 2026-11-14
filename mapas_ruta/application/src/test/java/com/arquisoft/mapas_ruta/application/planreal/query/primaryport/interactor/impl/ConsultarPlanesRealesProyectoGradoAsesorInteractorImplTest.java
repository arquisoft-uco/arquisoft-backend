package com.arquisoft.mapas_ruta.application.planreal.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealProyectoCriteria;
import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.model.ConsultarPlanesRealesProyectoGradoAsesorQuery;
import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealProyectoReadModel;
import com.arquisoft.mapas_ruta.application.planreal.query.usecase.ConsultarPlanesRealesProyectoGradoAsesorUseCase;
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
class ConsultarPlanesRealesProyectoGradoAsesorInteractorImplTest {

    @Mock
    private ConsultarPlanesRealesProyectoGradoAsesorUseCase useCase;

    @InjectMocks
    private ConsultarPlanesRealesProyectoGradoAsesorInteractorImpl interactor;

    @Test
    void debeConvertirQueryEnCriteriaYDelegar_cuandoSeEjecuta() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var query = ConsultarPlanesRealesProyectoGradoAsesorQuery.crear(proyectoGrado,
                ConsultaCriteriaQuery.crear(1, 5, List.of(), null));
        var esperado = PaginatedResult.<PlanRealProyectoReadModel>of(List.of(), 1, 5, 0);
        when(useCase.ejecutar(any(PlanRealProyectoCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(PlanRealProyectoCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(captor.getValue().getPagina()).isEqualTo(1);
        assertThat(captor.getValue().getTamanio()).isEqualTo(5);
    }
}
