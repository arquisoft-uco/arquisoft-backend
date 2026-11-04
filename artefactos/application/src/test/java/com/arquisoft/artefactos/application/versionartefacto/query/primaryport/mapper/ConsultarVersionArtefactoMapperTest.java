package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionArtefactoQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.RolConsultaVersionArtefacto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarVersionArtefactoMapperTest {

    @Test
    void debeMarcarSoloComoAsesor_cuandoRolAsesor() {
        // Arrange
        var query = ConsultarVersionArtefactoQuery.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                List.of(RolConsultaVersionArtefacto.ASESOR));

        // Act
        var criteria = ConsultarVersionArtefactoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.versionArtefacto()).isEqualTo(query.versionArtefacto());
        assertThat(criteria.actor()).isEqualTo(query.actor());
        assertThat(criteria.comoAsesor()).isTrue();
        assertThat(criteria.comoEstudiante()).isFalse();
    }

    @Test
    void debeMarcarAmbos_cuandoTieneLosDosRoles() {
        // Arrange
        var query = ConsultarVersionArtefactoQuery.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                List.of(RolConsultaVersionArtefacto.ASESOR, RolConsultaVersionArtefacto.ESTUDIANTE));

        // Act
        var criteria = ConsultarVersionArtefactoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.comoAsesor()).isTrue();
        assertThat(criteria.comoEstudiante()).isTrue();
    }
}
