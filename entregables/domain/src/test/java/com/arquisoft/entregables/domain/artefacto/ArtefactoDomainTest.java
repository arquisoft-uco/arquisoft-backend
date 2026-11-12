package com.arquisoft.entregables.domain.artefacto;

import com.arquisoft.shared.message.constant.EntregablesAlmacenamiento;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArtefactoDomainTest {

    @Test
    void debeCrearConClaveDestino_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var version = new ArtefactoDomain.VersionCerrada(UtilUUID.generarNuevoUUID(), 3, Instant.now());
        var repositorio = new ArtefactoDomain.DatosRepositorio(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repo");

        // Act
        var artefacto = ArtefactoDomain.crear(id, proyecto, "Documento", version, repositorio);

        // Assert
        assertThat(artefacto.getId()).isEqualTo(id);
        assertThat(artefacto.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(artefacto.getNombreDocumento()).isEqualTo("Documento");
        assertThat(artefacto.getNumeroVersion()).isEqualTo(3);
        assertThat(artefacto.getVersionArtefacto()).isEqualTo(version.versionArtefacto());
        assertThat(artefacto.getRepositorio()).isEqualTo(repositorio.repositorio());
        assertThat(artefacto.getNombreRepositorio()).isEqualTo("Repo");
        assertThat(artefacto.getClaveObjeto()).isEqualTo(EntregablesAlmacenamiento.PREFIJO_ARTEFACTOS + id + "/v3");
        assertThat(artefacto.estaOmitido()).isFalse();
        assertThat(artefacto.esVacio()).isFalse();
    }

    @Test
    void debeAcumularErroresDeCampo_cuandoFaltanDatos() {
        // Arrange
        var version = new ArtefactoDomain.VersionCerrada(null, 0, null);
        var repositorio = new ArtefactoDomain.DatosRepositorio(null, null, " ");

        // Act & Assert
        assertThatThrownBy(() -> ArtefactoDomain.crear(null, null, " ", version, repositorio))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(error -> error.campo()).contains(
                            EntregablesFields.Artefacto.ID,
                            EntregablesFields.Artefacto.PROYECTO_GRADO,
                            EntregablesFields.Artefacto.NOMBRE_DOCUMENTO,
                            EntregablesFields.Artefacto.VERSION_ARTEFACTO,
                            EntregablesFields.Artefacto.NUMERO_VERSION,
                            EntregablesFields.Artefacto.FECHA_VERSION,
                            EntregablesFields.Artefacto.VERSION_REPOSITORIO_ARTEFACTO,
                            EntregablesFields.Artefacto.REPOSITORIO,
                            EntregablesFields.Artefacto.NOMBRE_REPOSITORIO);
                    assertThat(errores).anySatisfy(error -> assertThat(error.codigoError())
                            .isEqualTo(EntregablesCodes.Artefacto.NUMERO_VERSION_INVALIDO));
                });
    }

    @Test
    void debeReconstruirSinRevalidar_cuandoLosDatosSonInvalidos() {
        // Arrange
        var version = new ArtefactoDomain.VersionCerrada(null, 0, null);
        var repositorio = new ArtefactoDomain.DatosRepositorio(null, null, null);
        var omision = new ArtefactoDomain.Omision(null, null);

        // Act
        var artefacto = ArtefactoDomain.reconstruir(null, null, null, "clave", version, repositorio, omision);

        // Assert
        assertThat(artefacto.getClaveObjeto()).isEqualTo("clave");
        assertThat(artefacto.getNumeroVersion()).isZero();
        assertThat(artefacto.esVacio()).isFalse();
    }

    @Test
    void debeIndicarOmisionYComparar_cuandoElArtefactoFueOmitido() {
        // Arrange
        var ocurrido = Instant.parse("2026-11-10T10:00:00Z");
        var version = new ArtefactoDomain.VersionCerrada(UtilUUID.generarNuevoUUID(), 1, Instant.now());
        var repositorio = new ArtefactoDomain.DatosRepositorio(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repo");
        var omitido = ArtefactoDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Doc",
                "clave", version, repositorio, new ArtefactoDomain.Omision(ocurrido, ocurrido));
        var vigente = ArtefactoDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Doc",
                "clave", version, repositorio, new ArtefactoDomain.Omision(null, null));

        // Act & Assert
        assertThat(omitido.estaOmitido()).isTrue();
        assertThat(omitido.tieneOmisionIgualOPosteriorA(ocurrido)).isTrue();
        assertThat(omitido.tieneOmisionIgualOPosteriorA(ocurrido.minusSeconds(1))).isTrue();
        assertThat(omitido.tieneOmisionIgualOPosteriorA(ocurrido.plusSeconds(1))).isFalse();
        assertThat(vigente.estaOmitido()).isFalse();
        assertThat(vigente.tieneOmisionIgualOPosteriorA(ocurrido)).isFalse();
    }

    @Test
    void debeSerVacio_cuandoSeUsaElCentinela() {
        // Act & Assert
        assertThat(ArtefactoDomain.VACIO.esVacio()).isTrue();
        assertThat(ArtefactoDomain.VACIO.estaOmitido()).isFalse();
    }
}
