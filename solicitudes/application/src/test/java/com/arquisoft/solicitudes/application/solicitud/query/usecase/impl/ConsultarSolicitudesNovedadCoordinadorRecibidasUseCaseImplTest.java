package com.arquisoft.solicitudes.application.solicitud.query.usecase.impl;

import com.arquisoft.solicitudes.application.remitente.query.readmodel.RemitenteReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.criteria.SolicitudCriteria;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.secondaryport.SolicitudQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarSolicitudesNovedadCoordinadorRecibidasUseCaseImplTest {

    @Mock
    private SolicitudQueryOutputPort solicitudQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarSolicitudesNovedadCoordinadorRecibidasUseCaseImpl useCase;

    private static SolicitudCriteria criteria() {
        return SolicitudCriteria.builder().pagina(0).tamanio(10).build();
    }

    @Test
    void debeConsultarElPuertoYRetornarSuResultado_cuandoSeEjecuta() {
        // Arrange
        var criteria = criteria();
        var remitente = new RemitenteReadModel(UUID.randomUUID(), "EST-1", "Ana", "ana@uco.edu.co");
        var solicitud = new SolicitudReadModel(UUID.randomUUID(), "una novedad",
                Instant.now(), "NOVEDAD_PARA_EL_COORDINADOR", "Novedad para el Coordinador",
                remitente);
        var esperado = PaginatedResult.of(List.of(solicitud), 0, 10, 1L);
        when(solicitudQueryOutputPort.consultar(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        assertThat(resultado.getContent()).hasSize(1);
        verify(solicitudQueryOutputPort).consultar(criteria);
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElPuertoNoEncuentraSolicitudes() {
        // Arrange
        var criteria = criteria();
        var vacio = PaginatedResult.of(List.<SolicitudReadModel>of(), 0, 10, 0L);
        when(solicitudQueryOutputPort.consultar(any(SolicitudCriteria.class))).thenReturn(vacio);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeLoguearEntradaYCierre_conSolicitudKey() {
        // Arrange
        var criteria = criteria();
        var vacio = PaginatedResult.of(List.<SolicitudReadModel>of(), 0, 10, 0L);
        when(solicitudQueryOutputPort.consultar(criteria)).thenReturn(vacio);

        // Act
        useCase.ejecutar(criteria);

        // Assert
        verify(logger).debug(eq(SolicitudKey.LOG_CONSULTANDO_NOVEDAD_COORDINADOR_RECIBIDAS),
                eq(0), eq(10), eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(SolicitudKey.LOG_CONSULTA_NOVEDAD_COORDINADOR_RECIBIDAS_COMPLETADA),
                eq(0L), eq(0), eq(10));
    }
}
