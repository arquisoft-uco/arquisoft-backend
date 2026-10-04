package com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.query.criteria.RespuestaCriteria;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasNovedadCoordinadorRecibidasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.usecase.ConsultarRespuestasNovedadCoordinadorRecibidasUseCase;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarRespuestasNovedadCoordinadorRecibidasInteractorImplTest {

    @Mock
    private ConsultarRespuestasNovedadCoordinadorRecibidasUseCase useCase;

    @Captor
    private ArgumentCaptor<RespuestaCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarRespuestasNovedadCoordinadorRecibidasInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElCriteriaDelMapper_yRetornarSuResultado() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarRespuestasNovedadCoordinadorRecibidasQuery.crear(estudiante, criterio);

        PaginatedResult<RespuestaReadModel> esperado = PaginatedResult.of(List.of(), 0, 10, 0L);
        when(useCase.ejecutar(any(RespuestaCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteriaCaptor.capture());

        var forzadoRemitente = NodoFiltro.predicado(
                RespuestaCriteria.Campo.REMITENTE_USUARIO_ID.getClave(),
                FiltroOperador.ES, estudiante.toString());
        var forzadoTipo = NodoFiltro.predicado(
                RespuestaCriteria.Campo.TIPO_SOLICITUD_ID.getClave(),
                FiltroOperador.ES, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId());
        assertThat(criteriaCaptor.getValue().getRaiz()).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(forzadoRemitente, forzadoTipo)));
    }
}
