package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapasRutaCoordinadorQuery;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaReadModel;
import com.arquisoft.mapas_ruta.application.maparuta.query.usecase.ConsultarMapasRutaCoordinadorUseCase;
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
class ConsultarMapasRutaCoordinadorInteractorImplTest {

    @Mock
    private ConsultarMapasRutaCoordinadorUseCase useCase;

    @InjectMocks
    private ConsultarMapasRutaCoordinadorInteractorImpl interactor;

    @Test
    void debeMapearAlCriteriaYDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarMapasRutaCoordinadorQuery.crear(coordinador, criterio);
        PaginatedResult<MapaRutaReadModel> esperado = PaginatedResult.of(List.of(), 0, 10, 0L);
        when(useCase.ejecutar(any(MapaRutaCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(MapaRutaCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getRaiz()).isEqualTo(
                NodoFiltro.predicado("coordinador", FiltroOperador.ES, coordinador.toString()));
    }
}
