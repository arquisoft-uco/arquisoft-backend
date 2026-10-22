package com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.query.criteria.RespuestaCriteria;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasCambioAsesorRecibidasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.usecase.ConsultarRespuestasCambioAsesorRecibidasUseCase;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarRespuestasCambioAsesorRecibidasInteractorImplTest {

    @Mock
    private ConsultarRespuestasCambioAsesorRecibidasUseCase useCase;

    @Captor
    private ArgumentCaptor<RespuestaCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarRespuestasCambioAsesorRecibidasInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseYRetornarSuResultado_cuandoElMapperConstruyeElCriteria() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarRespuestasCambioAsesorRecibidasQuery.crear(estudiante, criterio);

        var esperado = PaginatedResult.<RespuestaReadModel>of(List.of(), 0, 10, 0L);
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
                FiltroOperador.ES, TipoSolicitud.CAMBIO_DE_ASESOR.getId());
        assertThat(criteriaCaptor.getValue().getRaiz()).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(forzadoRemitente, forzadoTipo)));
    }
}
