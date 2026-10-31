package com.arquisoft.artefactos.application.estudianteartefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoOmitidoException;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.RemocionEstudianteArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudianteNoVinculadoArtefactoException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverEstudianteArtefactoValidatorImplTest {

    private final RemoverEstudianteArtefactoValidatorImpl validator = new RemoverEstudianteArtefactoValidatorImpl();

    private UUID asesorId;
    private UUID estudianteId;
    private RemocionEstudianteArtefactoDomain entrada;
    private ArtefactoDomain artefacto;
    private AsesorDomain asesor;
    private VersionRepositorioArtefactoDomain version;
    private EstudianteDomain estudiante;

    @BeforeEach
    void setUp() {
        asesorId = UtilUUID.generarNuevoUUID();
        estudianteId = UtilUUID.generarNuevoUUID();
        var versionId = UtilUUID.generarNuevoUUID();
        artefacto = ArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), versionId, asesorId, null);
        entrada = RemocionEstudianteArtefactoDomain.crear(artefacto.getId(), asesorId, estudianteId);
        asesor = AsesorDomain.reconstruir(asesorId, "1", "Ana Perez", "ana@uco.edu.co", Instant.now(), null);
        version = VersionRepositorioArtefactoDomain.reconstruir(
                versionId, "Doc", 1, UtilUUID.generarNuevoUUID(), "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
        estudiante = EstudianteDomain.reconstruir(estudianteId, "2", "Laura Gomez", "l@uco.edu.co", Instant.EPOCH, null);
    }

    @Test
    void debePasar_cuandoTodasLasReglasSeCumplen() {
        // Act & Assert
        assertThatCode(() -> validator.validar(entrada, artefacto, asesor, version, estudiante, true))
                .doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoElEstudianteEstaDadoDeBaja() {
        // Arrange
        var dadoDeBaja = EstudianteDomain.reconstruir(
                estudianteId, "2", "Laura Gomez", "l@uco.edu.co", Instant.EPOCH, Instant.now());

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada, artefacto, asesor, version, dadoDeBaja, true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarArtefactoNoEncontrado_antesQueElAsesorInexistente() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, ArtefactoDomain.VACIO, AsesorDomain.VACIO, version, estudiante, true))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoEncontrado_antesQueLaVersionInexistente() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, artefacto, AsesorDomain.VACIO, VersionRepositorioArtefactoDomain.VACIO, estudiante, true))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzarVersionNoEncontrada_antesQueElEstudianteInexistente() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, artefacto, asesor, VersionRepositorioArtefactoDomain.VACIO, EstudianteDomain.VACIO, true))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzarEstudianteNoEncontrado_antesQueLaPropiedadDelArtefacto() {
        // Arrange
        var ajeno = ArtefactoDomain.reconstruir(
                artefacto.getId(), artefacto.getProyectoGrado(), artefacto.getVersionRepositorioArtefacto(),
                UtilUUID.generarNuevoUUID(), null);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, ajeno, asesor, version, EstudianteDomain.VACIO, true))
                .isInstanceOf(EstudianteNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoPropietario_antesQueElArtefactoOmitido() {
        // Arrange
        var ajenoYOmitido = ArtefactoDomain.reconstruir(
                artefacto.getId(), artefacto.getProyectoGrado(), artefacto.getVersionRepositorioArtefacto(),
                UtilUUID.generarNuevoUUID(), Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, ajenoYOmitido, asesor, version, estudiante, false))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzarArtefactoOmitido_antesQueElVinculoDelEstudiante() {
        // Arrange
        var omitido = ArtefactoDomain.reconstruir(
                artefacto.getId(), artefacto.getProyectoGrado(), artefacto.getVersionRepositorioArtefacto(),
                asesorId, Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, omitido, asesor, version, estudiante, false))
                .isInstanceOf(ArtefactoOmitidoException.class);
    }

    @Test
    void debeLanzarEstudianteNoVinculado_cuandoLoDemasEsValido() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, artefacto, asesor, version, estudiante, false))
                .isInstanceOf(EstudianteNoVinculadoArtefactoException.class);
    }
}
