package com.arquisoft.solicitudes.application.solicitud.command.finder.impl;

import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity.DatosSolicitudEntity;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
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
class DatosSolicitudFinderImplTest {

    @Mock
    private SolicitudOutputPort solicitudOutputPort;

    @InjectMocks
    private DatosSolicitudFinderImpl finder;

    @Test
    void debeArmarElResumenSolicitud_cuandoElPuertoDevuelveLosDatos() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var remitenteUsuario = UUID.randomUUID();
        var destinatarioUsuario = UUID.randomUUID();
        when(solicitudOutputPort.buscarDatos(solicitud)).thenReturn(Optional.of(
                new DatosSolicitudEntity(remitenteUsuario, destinatarioUsuario,
                        TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId())));

        // Act
        var resumen = finder.obtener(solicitud);

        // Assert
        assertThat(resumen.esVacio()).isFalse();
        assertThat(resumen.solicitud()).isEqualTo(solicitud);
        assertThat(resumen.remitenteUsuario()).isEqualTo(remitenteUsuario);
        assertThat(resumen.destinatarioUsuario()).isEqualTo(destinatarioUsuario);
        assertThat(resumen.tipoSolicitud())
                .isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId());
    }

    @Test
    void debeDevolverVacio_cuandoElPuertoNoEncuentraLaSolicitud() {
        // Arrange
        var solicitud = UUID.randomUUID();
        when(solicitudOutputPort.buscarDatos(solicitud)).thenReturn(Optional.empty());

        // Act & Assert
        assertThat(finder.obtener(solicitud).esVacio()).isTrue();
    }
}
