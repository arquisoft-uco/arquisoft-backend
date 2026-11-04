package com.arquisoft.mapas_ruta.application.tarea.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.tarea.query.criteria.TareaMapaRutaCriteria;
import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarTareasMapaRutaEstudianteQuery;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.mapas_ruta.application.tarea.query.usecase.ConsultarTareasMapaRutaEstudianteUseCase;
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
class ConsultarTareasMapaRutaEstudianteInteractorImplTest {

    @Mock
    private ConsultarTareasMapaRutaEstudianteUseCase useCase;

    @InjectMocks
    private ConsultarTareasMapaRutaEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCase_conElCriteriaMapeado() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var query = ConsultarTareasMapaRutaEstudianteQuery.crear(mapaRuta,
                ConsultaCriteriaQuery.crear(1, 5, List.of(), null));
        var esperado = PaginatedResult.<TareaReadModel>of(List.of(), 1, 5, 0);
        when(useCase.ejecutar(any(TareaMapaRutaCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(TareaMapaRutaCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getMapaRuta()).isEqualTo(mapaRuta);
        assertThat(captor.getValue().getPagina()).isEqualTo(1);
        assertThat(captor.getValue().getTamanio()).isEqualTo(5);
    }
}
