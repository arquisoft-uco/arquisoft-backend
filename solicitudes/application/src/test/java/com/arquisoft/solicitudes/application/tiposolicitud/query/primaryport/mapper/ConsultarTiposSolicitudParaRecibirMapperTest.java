package com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudParaRecibirQuery;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarTiposSolicitudParaRecibirMapperTest {

    @Test
    void debeConservarActorYRoles_cuandoConvierteQueryACriteria() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var query = ConsultarTiposSolicitudParaRecibirQuery.crear(actor, List.of("COORDINADOR", "ASESOR"));

        // Act
        var criteria = ConsultarTiposSolicitudParaRecibirMapper.toCriteria(query);

        // Assert
        assertThat(criteria.actor()).isEqualTo(actor);
        assertThat(criteria.rolesIdentidad()).containsExactly("COORDINADOR", "ASESOR");
    }
}
