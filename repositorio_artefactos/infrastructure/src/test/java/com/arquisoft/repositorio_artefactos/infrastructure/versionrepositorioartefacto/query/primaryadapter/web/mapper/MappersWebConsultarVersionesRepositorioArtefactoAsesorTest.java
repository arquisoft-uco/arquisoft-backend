package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.RepositorioDeVersionReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoAsesorReadModel;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MappersWebConsultarVersionesRepositorioArtefactoAsesorTest {

    @Test
    void debeAplicarValoresPorDefecto_cuandoElDtoEsNulo() {
        // Act
        var query = ConsultarVersionesRepositorioArtefactoAsesorRequestMapper.toQuery(null);

        // Assert
        assertThat(query.pagina()).isZero();
        assertThat(query.tamanio()).isEqualTo(10);
        assertThat(query.ordenamiento()).isEmpty();
        assertThat(query.raiz()).isNull();
    }

    @Test
    void debeCopiarPaginaTamanioYOrden_cuandoElDtoLosTrae() {
        // Arrange
        var dto = new QueryCriteriaRequestDTO(2, 30, List.of("nombreRepositorio:DESC"), null);

        // Act
        var query = ConsultarVersionesRepositorioArtefactoAsesorRequestMapper.toQuery(dto);

        // Assert
        assertThat(query.pagina()).isEqualTo(2);
        assertThat(query.tamanio()).isEqualTo(30);
        assertThat(query.ordenamiento()).hasSize(1);
    }

    @Test
    void debeMapearCamposYRepositorioAnidadoSinUriArchivo_cuandoReadModelCompleto() {
        // Arrange
        var repositorio = new RepositorioDeVersionReadModel(UtilUUID.generarNuevoUUID(), "Repo", "Descripcion");
        var readModel = new VersionRepositorioArtefactoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Formato A", 2,
                Instant.parse("2026-10-20T10:00:00Z"), "application/pdf", 2048L, "formato.pdf", "a".repeat(64),
                repositorio);

        // Act
        var dto = VersionRepositorioArtefactoAsesorResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.nombreDocumento()).isEqualTo("Formato A");
        assertThat(dto.numeroVersion()).isEqualTo(2);
        assertThat(dto.fechaCreacion()).isEqualTo(readModel.fechaCreacion());
        assertThat(dto.tipoContenido()).isEqualTo("application/pdf");
        assertThat(dto.tamanoBytes()).isEqualTo(2048L);
        assertThat(dto.nombreArchivo()).isEqualTo("formato.pdf");
        assertThat(dto.hashSha256()).isEqualTo("a".repeat(64));
        assertThat(dto.repositorioArtefacto().id()).isEqualTo(repositorio.id());
        assertThat(dto.repositorioArtefacto().nombreRepositorio()).isEqualTo("Repo");
        assertThat(dto.repositorioArtefacto().descripcion()).isEqualTo("Descripcion");
        assertThat(Arrays.stream(dto.getClass().getRecordComponents()).map(c -> c.getName()))
                .doesNotContain("uriArchivo");
    }
}
