package com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudQuery;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarTiposSolicitudMapperTest {

    @Test
    void debeConservarActorYRoles_cuandoConvierteQueryACriteria() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var query = ConsultarTiposSolicitudQuery.crear(actor, List.of("ESTUDIANTE", "COORDINADOR"));

        // Act
        var criteria = ConsultarTiposSolicitudMapper.toCriteria(query);

        // Assert
        assertThat(criteria.actor()).isEqualTo(actor);
        assertThat(criteria.rolesIdentidad()).containsExactly("ESTUDIANTE", "COORDINADOR");
    }
}
