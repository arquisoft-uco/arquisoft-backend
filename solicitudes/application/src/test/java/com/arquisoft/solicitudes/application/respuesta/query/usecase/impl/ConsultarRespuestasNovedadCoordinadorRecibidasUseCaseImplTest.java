package com.arquisoft.solicitudes.application.respuesta.query.usecase.impl;

import com.arquisoft.solicitudes.application.destinatario.query.readmodel.DestinatarioReadModel;
import com.arquisoft.solicitudes.application.remitente.query.readmodel.RemitenteReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.criteria.RespuestaCriteria;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.secondaryport.RespuestaQueryOutputPort;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarRespuestasNovedadCoordinadorRecibidasUseCaseImplTest {

    @Mock
    private RespuestaQueryOutputPort respuestaQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarRespuestasNovedadCoordinadorRecibidasUseCaseImpl useCase;

    private static RespuestaCriteria criteria() {
        return RespuestaCriteria.builder().pagina(0).tamanio(10).build();
    }

    private static RespuestaReadModel respuesta() {
        var remitente = new RemitenteReadModel(UUID.randomUUID(), "EST-1", "Ana", "ana@uco.edu.co");
        var destinatario = new DestinatarioReadModel(
                UUID.randomUUID(), "COORD-1", "Coordinadora", "coord@uco.edu.co");
        var solicitud = new SolicitudReadModel(UUID.randomUUID(), "una novedad",
                Instant.now(), "NOVEDAD_PARA_EL_COORDINADOR", "Novedad para el Coordinador",
                remitente, destinatario);
        return new RespuestaReadModel(UUID.randomUUID(), "contenido de la respuesta",
                LocalDateTime.now(), "EN_REVISION", "En revisión", solicitud);
    }

    @Test
    void debeConsultarElPuertoYRetornarSuResultado_cuandoSeEjecuta() {
        // Arrange
        var criteria = criteria();
        var esperado = PaginatedResult.of(List.of(respuesta()), 0, 10, 1L);
        when(respuestaQueryOutputPort.consultar(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        assertThat(resultado.getContent()).hasSize(1);
        verify(respuestaQueryOutputPort).consultar(criteria);
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElPuertoNoEncuentraRespuestas() {
        // Arrange
        var criteria = criteria();
        var vacio = PaginatedResult.of(List.<RespuestaReadModel>of(), 0, 10, 0L);
        when(respuestaQueryOutputPort.consultar(any(RespuestaCriteria.class))).thenReturn(vacio);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeLoguearEntradaYCierre_conRespuestaKey() {
        // Arrange
        var criteria = criteria();
        var vacio = PaginatedResult.of(List.<RespuestaReadModel>of(), 0, 10, 0L);
        when(respuestaQueryOutputPort.consultar(criteria)).thenReturn(vacio);

        // Act
        useCase.ejecutar(criteria);

        // Assert
        verify(logger).debug(eq(RespuestaKey.LOG_CONSULTANDO_NOVEDAD_COORDINADOR_RECIBIDAS),
                eq(0), eq(10), eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(RespuestaKey.LOG_CONSULTA_NOVEDAD_COORDINADOR_RECIBIDAS_COMPLETADA),
                eq(0L), eq(0), eq(10));
    }
}
