package com.arquisoft.solicitudes.application.tiposolicitud.query.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.TipoSolicitudKey;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.tiposolicitud.query.criteria.TipoSolicitudParaRecibirCriteria;
import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.solicitudes.application.tiposolicitud.query.secondaryport.TipoSolicitudQueryOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarTiposSolicitudParaRecibirUseCaseImplTest {

    @Mock
    private TipoSolicitudQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarTiposSolicitudParaRecibirUseCaseImpl useCase;

    @Test
    void debeConsultarPorRoles_cuandoHayRoles() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var roles = List.of("COORDINADOR");
        var esperados = List.of(
                new TipoSolicitudReadModel("CAMBIO_DE_ASESOR", "Cambio de Asesor",
                        "Solicitud para modificar el asesor"),
                new TipoSolicitudReadModel("AMPLIACION_DE_PLAZO", "Ampliacion de Plazo",
                        "Solicitud para extender la fecha de entrega del proyecto"));
        when(queryOutputPort.consultarRecibiblesPorRoles(roles)).thenReturn(esperados);

        // Act
        var resultado = useCase.ejecutar(new TipoSolicitudParaRecibirCriteria(actor, roles));

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(queryOutputPort, times(1)).consultarRecibiblesPorRoles(roles);
        verify(logger).debug(TipoSolicitudKey.LOG_CONSULTANDO_PARA_RECIBIR, actor, roles);
        verify(logger).debug(TipoSolicitudKey.LOG_CONSULTA_PARA_RECIBIR_COMPLETADA, 2);
    }

    @Test
    void debeRetornarListaVacia_cuandoElPuertoNoDevuelveTipos() {
        // Arrange
        var roles = List.of("ASESOR");
        when(queryOutputPort.consultarRecibiblesPorRoles(roles)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(new TipoSolicitudParaRecibirCriteria(UtilUUID.generarNuevoUUID(), roles));

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(TipoSolicitudKey.LOG_CONSULTA_PARA_RECIBIR_COMPLETADA, 0);
    }

    @Test
    void debeInvocarElPuertoConListaVacia_cuandoNoHayRoles() {
        // Arrange
        var roles = List.<String>of();
        when(queryOutputPort.consultarRecibiblesPorRoles(roles)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(new TipoSolicitudParaRecibirCriteria(UtilUUID.generarNuevoUUID(), roles));

        // Assert
        assertThat(resultado).isEmpty();
        verify(queryOutputPort, times(1)).consultarRecibiblesPorRoles(roles);
    }
}
