package com.arquisoft.solicitudes.application.tiposolicitud.query.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.TipoSolicitudKey;
import com.arquisoft.solicitudes.application.tiposolicitud.query.criteria.TipoSolicitudCriteria;
import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.solicitudes.application.tiposolicitud.query.secondaryport.TipoSolicitudQueryOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarTiposSolicitudUseCaseImplTest {

    @Mock
    private TipoSolicitudQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarTiposSolicitudUseCaseImpl useCase;

    @Test
    void debeConsultarPorIds_cuandoHayTiposPermitidos() {
        // Arrange
        var tipos = Set.of("CAMBIO_DE_ASESOR", "AMPLIACION_DE_PLAZO");
        var esperados = List.of(
                new TipoSolicitudReadModel("CAMBIO_DE_ASESOR", "Cambio de Asesor",
                        "Solicitud para modificar el asesor"),
                new TipoSolicitudReadModel("AMPLIACION_DE_PLAZO", "Ampliación de Plazo",
                        "Solicitud para extender la fecha de entrega del proyecto"));
        when(queryOutputPort.consultarPorIds(tipos)).thenReturn(esperados);

        // Act
        var resultado = useCase.ejecutar(new TipoSolicitudCriteria(tipos));

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(queryOutputPort, times(1)).consultarPorIds(tipos);
        verify(logger).debug(eq(TipoSolicitudKey.LOG_CONSULTANDO), eq(2));
        verify(logger).debug(eq(TipoSolicitudKey.LOG_CONSULTA_COMPLETADA), eq(2));
    }

    @Test
    void debeRetornarListaVaciaSinConsultar_cuandoConjuntoEsVacio() {
        // Act
        var resultado = useCase.ejecutar(new TipoSolicitudCriteria(Set.of()));

        // Assert
        assertThat(resultado).isEmpty();
        verifyNoInteractions(queryOutputPort);
        verify(logger).debug(eq(TipoSolicitudKey.LOG_CONSULTANDO), eq(0));
        verify(logger).debug(eq(TipoSolicitudKey.LOG_CONSULTA_COMPLETADA), eq(0));
    }

    @Test
    void debeRetornarListaVacia_cuandoElPuertoNoEncuentraTipos() {
        // Arrange
        var tipos = Set.of("CAMBIO_DE_ASESOR");
        when(queryOutputPort.consultarPorIds(tipos)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(new TipoSolicitudCriteria(tipos));

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(TipoSolicitudKey.LOG_CONSULTA_COMPLETADA), eq(0));
    }
}
