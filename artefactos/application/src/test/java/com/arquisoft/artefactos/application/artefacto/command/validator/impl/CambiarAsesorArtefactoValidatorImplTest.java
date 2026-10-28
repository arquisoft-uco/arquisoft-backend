package com.arquisoft.artefactos.application.artefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.CambioAsesorArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoVigenteException;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarAsesorArtefactoValidatorImplTest {

    private final CambiarAsesorArtefactoValidatorImpl validator = new CambiarAsesorArtefactoValidatorImpl();

    private UUID proyecto;
    private UUID asesorId;
    private CambioAsesorArtefactoDomain cambio;
    private ArtefactoDomain artefacto;
    private AsesorDomain asesorVigente;
    private AsesorProyectoGradoDomain vinculoVigente;

    @BeforeEach
    void setUp() {
        proyecto = UtilUUID.generarNuevoUUID();
        asesorId = UtilUUID.generarNuevoUUID();
        var artefactoId = UtilUUID.generarNuevoUUID();
        cambio = CambioAsesorArtefactoDomain.crear(artefactoId, asesorId);
        artefacto = ArtefactoDomain.reconstruir(
                artefactoId, proyecto, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        asesorVigente = AsesorDomain.reconstruir(
                asesorId, "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now(), null);
        vinculoVigente = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), asesorId, proyecto, Instant.now(), null);
    }

    @Test
    void debePasar_cuandoTodasLasReglasSeCumplen() {
        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, artefacto, asesorVigente, List.of(vinculoVigente)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarArtefactoNoEncontrado_antesQueLasDemasReglas_cuandoTodoFalla() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, ArtefactoDomain.VACIO, AsesorDomain.VACIO, List.of()))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoEncontrado_antesQueElVinculo_cuandoElAsesorNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, artefacto, AsesorDomain.VACIO, List.of()))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoVigente_antesQueElVinculo_cuandoElAsesorEstaDadoDeBaja() {
        // Arrange
        var eliminado = AsesorDomain.reconstruir(
                asesorId, "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now(), Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, artefacto, eliminado, List.of()))
                .isInstanceOf(AsesorNoVigenteException.class);
    }

    @Test
    void debeLanzarAsesorNoAsignado_cuandoNoHayVinculoVigenteDelAsesorEnElProyecto() {
        // Arrange
        var finalizado = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), asesorId, proyecto, Instant.now(), Instant.now());
        var deOtroAsesor = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), proyecto, Instant.now(), null);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, artefacto, asesorVigente, List.of(finalizado)))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
        assertThatThrownBy(() -> validator.validar(cambio, artefacto, asesorVigente, List.of(deOtroAsesor)))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }
}
