package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.DescargarArchivoVersionArtefactoQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.RolConsultaVersionArtefacto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DescargarArchivoVersionArtefactoMapperTest {

    @Test
    void debeConvertirRolesEnBanderas() {
        // Arrange
        var soloAsesor = DescargarArchivoVersionArtefactoQuery.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                List.of(RolConsultaVersionArtefacto.ASESOR));
        var ambos = DescargarArchivoVersionArtefactoQuery.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                List.of(RolConsultaVersionArtefacto.ASESOR, RolConsultaVersionArtefacto.ESTUDIANTE));

        // Act
        var criteriaAsesor = DescargarArchivoVersionArtefactoMapper.toCriteria(soloAsesor);
        var criteriaAmbos = DescargarArchivoVersionArtefactoMapper.toCriteria(ambos);

        // Assert
        assertThat(criteriaAsesor.versionArtefacto()).isEqualTo(soloAsesor.versionArtefacto());
        assertThat(criteriaAsesor.actor()).isEqualTo(soloAsesor.actor());
        assertThat(criteriaAsesor.comoAsesor()).isTrue();
        assertThat(criteriaAsesor.comoEstudiante()).isFalse();
        assertThat(criteriaAmbos.comoAsesor()).isTrue();
        assertThat(criteriaAmbos.comoEstudiante()).isTrue();
    }
}
