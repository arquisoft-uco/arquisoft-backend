package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarRespuestaAmpliacionPlazoMapperTest {

    @Test
    void debeFijarTipoAmpliacionDePlazo_cuandoMapeaElCommand() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var command = EliminarRespuestaAmpliacionPlazoCommand.crear(solicitud.toString(), coordinador);

        // Act
        var dominio = EliminarRespuestaAmpliacionPlazoMapper.toDomain(command);

        // Assert
        assertThat(dominio.getSolicitud()).isEqualTo(solicitud);
        assertThat(dominio.getResponsableUsuario()).isEqualTo(coordinador);
        assertThat(dominio.getTipoEsperado()).isEqualTo(TipoSolicitud.AMPLIACION_DE_PLAZO);
    }
}
