package com.arquisoft.artefactos.application.artefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.OmisionArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoOmitidoException;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OmitirArtefactoValidatorImplTest {

    private final OmitirArtefactoValidatorImpl validator = new OmitirArtefactoValidatorImpl();

    private UUID asesorId;
    private UUID versionId;
    private OmisionArtefactoDomain omision;
    private ArtefactoDomain artefacto;
    private AsesorDomain asesor;
    private VersionRepositorioArtefactoDomain version;

    @BeforeEach
    void setUp() {
        asesorId = UtilUUID.generarNuevoUUID();
        versionId = UtilUUID.generarNuevoUUID();
        var artefactoId = UtilUUID.generarNuevoUUID();
        omision = OmisionArtefactoDomain.crear(artefactoId, asesorId);
        artefacto = ArtefactoDomain.reconstruir(
                artefactoId, UtilUUID.generarNuevoUUID(), versionId, asesorId, null);
        asesor = AsesorDomain.reconstruir(asesorId, "20161020123", "Ana Perez", "ana@uco.edu.co", UtilFecha.generarInstanteActual(), null);
        version = VersionRepositorioArtefactoDomain.reconstruir(
                versionId, "Doc", 1, UtilUUID.generarNuevoUUID(), "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
    }

    private ArtefactoDomain omitidoDe(UUID asesorDelArtefacto) {
        return ArtefactoDomain.reconstruir(
                artefacto.getId(), artefacto.getProyectoGrado(), versionId, asesorDelArtefacto, UtilFecha.generarInstanteActual());
    }

    @Test
    void debePasar_cuandoTodasLasReglasSeCumplen() {
        // Act & Assert
        assertThatCode(() -> validator.validar(omision, artefacto, asesor, version)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarArtefactoNoEncontrado_antesQueLasDemasReglas_cuandoTodoFalla() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                omision, ArtefactoDomain.VACIO, AsesorDomain.VACIO, VersionRepositorioArtefactoDomain.VACIO))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoEncontrado_antesQueLaPropiedad_cuandoElAsesorNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                omision, omitidoDe(UtilUUID.generarNuevoUUID()), AsesorDomain.VACIO,
                VersionRepositorioArtefactoDomain.VACIO))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoPropietario_antesQueElArtefactoOmitido_cuandoOtroAsesorEsElResponsable() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                omision, omitidoDe(UtilUUID.generarNuevoUUID()), asesor, VersionRepositorioArtefactoDomain.VACIO))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzarArtefactoOmitido_antesQueLaVersion_cuandoElArtefactoYaEstaOmitido() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                omision, omitidoDe(asesorId), asesor, VersionRepositorioArtefactoDomain.VACIO))
                .isInstanceOf(ArtefactoOmitidoException.class);
    }

    @Test
    void debeLanzarVersionNoEncontrada_cuandoLaVersionDelRepositorioNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                omision, artefacto, asesor, VersionRepositorioArtefactoDomain.VACIO))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
    }
}
