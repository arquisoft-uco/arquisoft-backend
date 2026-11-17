package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDescargaDocumentacionReadModel;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EnlaceDescargaDocumentacionResponseMapperTest {

    @Test
    void debeMapearUrlYExpiraEn_cuandoRecibeElReadModel() {
        // Arrange
        var expiraEn = Instant.parse("2026-11-14T12:00:00Z");
        var readModel = new EnlaceDescargaDocumentacionReadModel("https://minio.test/firmado", expiraEn);

        // Act
        var dto = EnlaceDescargaDocumentacionResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.url()).isEqualTo("https://minio.test/firmado");
        assertThat(dto.expiraEn()).isEqualTo(expiraEn);
    }
}
