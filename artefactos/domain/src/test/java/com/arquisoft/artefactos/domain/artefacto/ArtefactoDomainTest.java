package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArtefactoDomainTest {

    @Test
    void debeCrearConIdGenerado_cuandoDatosValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var artefacto = ArtefactoDomain.crear(proyecto, asesor, List.of(version)).getFirst();

        // Assert
        assertThat(UtilUUID.esPorDefecto(artefacto.getId())).isFalse();
        assertThat(artefacto.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(artefacto.getVersionRepositorioArtefacto()).isEqualTo(version);
        assertThat(artefacto.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoProyectoVersionYAsesorSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> ArtefactoDomain.crear(null, null, Collections.singletonList(null)))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(error -> error.campo()).containsExactlyInAnyOrder(
                            ArtefactosFields.Artefacto.PROYECTO_GRADO,
                            ArtefactosFields.Artefacto.VERSION_REPOSITORIO_ARTEFACTO,
                            ArtefactosFields.Artefacto.ASESOR);
                    assertThat(errores).extracting(error -> error.codigoError()).containsExactlyInAnyOrder(
                            ArtefactosCodes.Artefacto.PROYECTO_GRADO_REQUERIDO,
                            ArtefactosCodes.Artefacto.VERSION_REPOSITORIO_ARTEFACTO_REQUERIDA,
                            ArtefactosCodes.Artefacto.ASESOR_REQUERIDO);
                });
    }


    @Test
    void debeCrearUnArtefactoPorVersion_cuandoDatosValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var versiones = List.of(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act
        var artefactos = ArtefactoDomain.crear(proyecto, asesor, versiones);

        // Assert
        assertThat(artefactos).extracting(ArtefactoDomain::getVersionRepositorioArtefacto)
                .containsExactlyElementsOf(versiones);
        assertThat(artefactos).extracting(ArtefactoDomain::getProyectoGrado).containsOnly(proyecto);
        assertThat(artefactos).extracting(ArtefactoDomain::getAsesor).containsOnly(asesor);
        assertThat(artefactos).extracting(ArtefactoDomain::getId).doesNotHaveDuplicates();
    }

    @Test
    void debeLanzar_cuandoVersionesSonNulasOVacias() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var vacias = List.<UUID>of();

        // Act & Assert
        for (var versiones : Arrays.asList(null, vacias)) {
            assertThatThrownBy(() -> ArtefactoDomain.crear(proyecto, asesor, versiones))
                    .isInstanceOf(DomainValidationException.class)
                    .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                            .singleElement()
                            .satisfies(error -> {
                                assertThat(error.campo())
                                        .isEqualTo(ArtefactosFields.Artefacto.VERSIONES_REPOSITORIO_ARTEFACTO);
                                assertThat(error.codigoError())
                                        .isEqualTo(ArtefactosCodes.Artefacto.VERSIONES_REQUERIDAS);
                            }));
        }
    }

    @Test
    void debeReconstruirSinValidar_cuandoLosDatosVienenDePersistencia() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var artefacto = ArtefactoDomain.reconstruir(id, null, null, null);

        // Assert
        assertThat(artefacto.getId()).isEqualTo(id);
        assertThat(artefacto.getProyectoGrado()).isNull();
    }
}
