package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;


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
        var artefacto = ArtefactoDomain.crear(proyecto, asesor, version);

        // Assert
        assertThat(UtilUUID.esPorDefecto(artefacto.getId())).isFalse();
        assertThat(artefacto.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(artefacto.getVersionRepositorioArtefacto()).isEqualTo(version);
        assertThat(artefacto.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoProyectoVersionYAsesorSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> ArtefactoDomain.crear(null, null, null))
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
    void debeReconstruirSinValidar_cuandoLosDatosVienenDePersistencia() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var artefacto = ArtefactoDomain.reconstruir(id, null, null, null, null);

        // Assert
        assertThat(artefacto.getId()).isEqualTo(id);
        assertThat(artefacto.getProyectoGrado()).isNull();
    }

    @Test
    void debeSerVacioSoloElCentinela_cuandoSeConsultaEsVacio() {
        // Arrange
        var reconstruido = ArtefactoDomain.reconstruir(
                UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(),
                UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), null);

        // Act & Assert
        assertThat(ArtefactoDomain.VACIO.esVacio()).isTrue();
        assertThat(reconstruido.esVacio()).isFalse();
    }

    @Test
    void debeCrearActivo_cuandoEsNuevo() {
        // Act
        var artefacto = ArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(artefacto.estaOmitido()).isFalse();
        assertThat(artefacto.getOmitidoEn()).isEqualTo(UtilFecha.VACIO);
    }

    @Test
    void debeMarcarOmitido_cuandoSeOmite() {
        // Arrange
        var artefacto = ArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var instante = UtilFecha.generarInstanteActual();

        // Act
        artefacto.omitir(instante);

        // Assert
        assertThat(artefacto.estaOmitido()).isTrue();
        assertThat(artefacto.getOmitidoEn()).isEqualTo(instante);
    }

    @Test
    void debeAsignarVersionRepositorio_cuandoCambiarVersionRepositorio() {
        // Arrange
        var artefacto = ArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var nueva = UtilUUID.generarNuevoUUID();

        // Act
        artefacto.cambiarVersionRepositorio(nueva);

        // Assert
        assertThat(artefacto.getVersionRepositorioArtefacto()).isEqualTo(nueva);
    }

    @Test
    void debeReconstruirComoActivo_cuandoOmitidoEnEsNulo() {
        // Act
        var artefacto = ArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), null);

        // Assert
        assertThat(artefacto.estaOmitido()).isFalse();
        assertThat(artefacto.getOmitidoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(ArtefactoDomain.VACIO.estaOmitido()).isFalse();
    }
}
