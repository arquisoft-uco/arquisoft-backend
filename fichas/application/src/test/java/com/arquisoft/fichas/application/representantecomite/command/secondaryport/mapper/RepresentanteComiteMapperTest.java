package com.arquisoft.fichas.application.representantecomite.command.secondaryport.mapper;

import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class RepresentanteComiteMapperTest {

    @Test
    void debeConservarLosCampos_cuandoSeMapeaDeDomainAEntityYDeVuelta() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-20T10:00:00Z");
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var representanteComite = RepresentanteComiteDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn, eliminadoEn);

        // Act
        var entity = RepresentanteComiteMapper.toEntity(representanteComite);
        var domain = RepresentanteComiteMapper.toDomain(entity);

        // Assert
        assertThat(entity.id()).isEqualTo(id);
        assertThat(entity.identificador()).isEqualTo("20161020123");
        assertThat(entity.nombre()).isEqualTo("Ana Perez");
        assertThat(entity.email()).isEqualTo("ana@uco.edu.co");
        assertThat(entity.ocurridoEn()).isEqualTo(ocurridoEn);
        assertThat(entity.eliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getNombre()).isEqualTo("Ana Perez");
        assertThat(domain.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(domain.getEliminadoEn()).isEqualTo(eliminadoEn);
    }
}
