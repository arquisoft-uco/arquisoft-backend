package com.arquisoft.artefactos.domain.versionartefacto;

import com.arquisoft.shared.message.constant.ArtefactosAlmacenamiento;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionArtefactoDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var generacion = GeneracionVersionArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), artefacto, UtilUUID.generarNuevoUUID());
        var antes = Instant.now();

        // Act
        var version = VersionArtefactoDomain.crear(generacion, 4);

        // Assert
        assertThat(UtilUUID.esPorDefecto(version.getId())).isFalse();
        assertThat(version.getArtefacto()).isEqualTo(artefacto);
        assertThat(version.getNumeroVersion()).isEqualTo(5);
        assertThat(version.getUriArchivo()).isEqualTo(ArtefactosAlmacenamiento.PREFIJO_VERSIONES + version.getId());
        assertThat(version.getFechaCreacion()).isAfterOrEqualTo(antes);
    }

    @Test
    void debeCrearVersionUno_cuandoNoHayVersionesPrevias() {
        // Arrange
        var generacion = GeneracionVersionArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act
        var version = VersionArtefactoDomain.crear(generacion, 0);

        // Assert
        assertThat(version.getNumeroVersion()).isEqualTo(1);
    }

    @Test
    void debeReconstruir_conTodosLosCampos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var fecha = Instant.parse("2026-10-28T10:00:00Z");

        // Act
        var version = VersionArtefactoDomain.reconstruir(id, artefacto, 3, fecha, "versiones/uri");

        // Assert
        assertThat(version.getId()).isEqualTo(id);
        assertThat(version.getArtefacto()).isEqualTo(artefacto);
        assertThat(version.getNumeroVersion()).isEqualTo(3);
        assertThat(version.getFechaCreacion()).isEqualTo(fecha);
        assertThat(version.getUriArchivo()).isEqualTo("versiones/uri");
        assertThat(version.esVacio()).isFalse();
    }

    @Test
    void debeSerVacio_cuandoEsCentinela() {
        // Act & Assert
        assertThat(VersionArtefactoDomain.VACIO.esVacio()).isTrue();
        assertThat(UtilUUID.esPorDefecto(VersionArtefactoDomain.VACIO.getId())).isTrue();
        assertThat(VersionArtefactoDomain.VACIO.getNumeroVersion()).isZero();
    }

    @Test
    void debeAcumularErrores_cuandoUltimoNumeroNegativo() {
        // Arrange
        var generacion = GeneracionVersionArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> VersionArtefactoDomain.crear(generacion, -1))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("campo")
                            .contains(ArtefactosFields.VersionArtefacto.NUMERO_VERSION);
                    assertThat(errores).extracting("codigoError")
                            .contains(ArtefactosCodes.VersionArtefacto.NUMERO_VERSION_INVALIDO);
                });
    }
}
