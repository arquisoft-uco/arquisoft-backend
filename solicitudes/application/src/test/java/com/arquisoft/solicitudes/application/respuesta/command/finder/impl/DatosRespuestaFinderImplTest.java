package com.arquisoft.solicitudes.application.respuesta.command.finder.impl;

import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatosRespuestaFinderImplTest {

    @Mock
    private RespuestaOutputPort respuestaOutputPort;

    @InjectMocks
    private DatosRespuestaFinderImpl finder;

    @Test
    void debeDelegarEnElPuertoYArmarElResumen_cuandoLaRespuestaExiste() {
        // Arrange
        var solicitud = UUID.randomUUID();
        when(respuestaOutputPort.buscarEstadoPorSolicitud(solicitud))
                .thenReturn(Optional.of("EN_REVISION"));

        // Act
        var resultado = finder.obtener(solicitud);

        // Assert
        assertThat(resultado).isEqualTo(new ResumenRespuesta(solicitud, "EN_REVISION"));
    }

    @Test
    void debeRetornarVacio_cuandoElPuertoNoEncuentraLaRespuesta() {
        // Arrange
        var solicitud = UUID.randomUUID();
        when(respuestaOutputPort.buscarEstadoPorSolicitud(solicitud)).thenReturn(Optional.empty());

        // Act & Assert
        assertThat(finder.obtener(solicitud).esVacio()).isTrue();
    }
}
