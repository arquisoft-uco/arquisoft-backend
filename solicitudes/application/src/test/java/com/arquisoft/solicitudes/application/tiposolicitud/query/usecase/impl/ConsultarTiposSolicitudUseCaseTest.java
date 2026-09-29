package com.arquisoft.solicitudes.application.tiposolicitud.query.usecase.impl;

import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.solicitudes.application.tiposolicitud.query.secondaryport.TipoSolicitudQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.TipoSolicitudKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarTiposSolicitudUseCaseTest {

    @Mock
    private TipoSolicitudQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarTiposSolicitudUseCaseImpl useCase;

    @Test
    void debeRetornarListaCompleta_cuandoExistenTiposDeSolicitud() {
        // Arrange
        var tiposEsperados = List.of(
                new TipoSolicitudReadModel("NOVEDAD_PARA_EL_COORDINADOR", "Novedad para el Coordinador",
                        "Solicitud para temas que surgen de improvisto y que no están tipados"),
                new TipoSolicitudReadModel("NOVEDAD_PARA_EL_ASESOR", "Novedad para el Asesor",
                        "Solicitud para temas que surgen de improvisto y que no están tipados"),
                new TipoSolicitudReadModel("CAMBIO_DE_ASESOR", "Cambio de Asesor",
                        "Solicitud para modificar el asesor"),
                new TipoSolicitudReadModel("AMPLIACION_DE_PLAZO", "Ampliación de Plazo",
                        "Solicitud para extender la fecha de entrega del proyecto"),
                new TipoSolicitudReadModel("REGISTRO_Y_MODIFICACION_DE_USUARIOS",
                        "Registro y modificación de Usuarios",
                        "Solicitud para el registro o modificación de usuarios")
        );
        when(queryOutputPort.findAll()).thenReturn(tiposEsperados);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(5);
        assertThat(resultado).containsExactlyElementsOf(tiposEsperados);
        verify(queryOutputPort, times(1)).findAll();
        verify(logger).debug(eq(TipoSolicitudKey.LOG_CONSULTA_COMPLETADA), eq(tiposEsperados.size()));
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayTiposDeSolicitud() {
        // Arrange
        when(queryOutputPort.findAll()).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
        verify(queryOutputPort, times(1)).findAll();
        verify(logger).debug(eq(TipoSolicitudKey.LOG_CONSULTA_COMPLETADA), eq(0));
    }
}
