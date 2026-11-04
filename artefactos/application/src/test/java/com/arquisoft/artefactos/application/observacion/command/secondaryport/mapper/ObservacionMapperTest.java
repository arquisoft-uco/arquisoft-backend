package com.arquisoft.artefactos.application.observacion.command.secondaryport.mapper;

import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionMapperTest {

    @Test
    void debeLlevarElOrigenEnAmbosSentidos() {
        // Arrange
        var origen = UtilUUID.generarNuevoUUID();
        var domain = ObservacionDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Texto", EstadoObservacion.NUEVA, origen);

        // Act
        var entity = ObservacionMapper.toEntity(domain);
        var reconstruida = ObservacionMapper.toDomain(entity);

        // Assert
        assertThat(entity.observacionOrigen()).isEqualTo(origen);
        assertThat(reconstruida.getObservacionOrigen()).isEqualTo(origen);
        assertThat(reconstruida.esContinuacion()).isTrue();
    }
}
