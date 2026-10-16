package com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaCambioAsesorCommand;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarRespuestaCambioAsesorMapperTest {

    @Test
    void debeFijarTipoCambioDeAsesor_cuandoMapeaElCommand() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var command = EliminarRespuestaCambioAsesorCommand.crear(solicitud.toString(), coordinador);

        // Act
        var dominio = EliminarRespuestaCambioAsesorMapper.toDomain(command);

        // Assert
        assertThat(dominio.getSolicitud()).isEqualTo(solicitud);
        assertThat(dominio.getResponsableUsuario()).isEqualTo(coordinador);
        assertThat(dominio.getTipoEsperado()).isEqualTo(TipoSolicitud.CAMBIO_DE_ASESOR);
    }
}
