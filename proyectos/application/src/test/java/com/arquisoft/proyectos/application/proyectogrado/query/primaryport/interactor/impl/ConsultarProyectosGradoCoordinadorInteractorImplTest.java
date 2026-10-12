package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoCoordinadorQuery;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.usecase.ConsultarProyectosGradoCoordinadorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
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
class ConsultarProyectosGradoCoordinadorInteractorImplTest {

    @Mock
    private ConsultarProyectosGradoCoordinadorUseCase consultarProyectosGradoCoordinadorUseCase;

    @InjectMocks
    private ConsultarProyectosGradoCoordinadorInteractorImpl interactor;

    @Test
    void debeConvertirQueryACriteriaYDelegar_cuandoSeEjecuta() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var query = ConsultarProyectosGradoCoordinadorQuery.crear(coordinador,
                ConsultaCriteriaQuery.crear(0, 10, List.of(), null));
        var esperado = PaginatedResult.of(List.<ProyectoGradoReadModel>of(), 0, 10, 0L);
        when(consultarProyectosGradoCoordinadorUseCase.ejecutar(any(ProyectoGradoCriteria.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(ProyectoGradoCriteria.class);
        verify(consultarProyectosGradoCoordinadorUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getRaiz()).isEqualTo(NodoFiltro.predicado(
                ProyectoGradoCriteria.Campo.COORDINADOR.getClave(), FiltroOperador.ES, coordinador.toString()));
    }
}
