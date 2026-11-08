package com.arquisoft.artefactos.application.artefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.ReactivacionArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.model.PesoArtefactoReactivable;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudiantesSuperanProyectoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.CupoEstudiantesArtefacto;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.CuotaProyectoGradoExcedidaException;
import com.arquisoft.artefactos.domain.versionartefacto.model.AlmacenamientoProyectoGrado;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReactivarArtefactoValidatorImplTest {

    private final ReactivarArtefactoValidatorImpl validator = new ReactivarArtefactoValidatorImpl();

    private UUID proyecto;
    private UUID asesorId;
    private UUID estudiante;
    private ReactivacionArtefactoDomain reactivacion;
    private AsesorDomain asesorVigente;
    private AsesorProyectoGradoDomain vinculoVigente;
    private CupoEstudiantesArtefacto cupoValido;
    private PesoArtefactoReactivable peso;
    private AlmacenamientoProyectoGrado almacenamiento;

    @BeforeEach
    void setUp() {
        proyecto = UtilUUID.generarNuevoUUID();
        asesorId = UtilUUID.generarNuevoUUID();
        estudiante = UtilUUID.generarNuevoUUID();
        reactivacion = ReactivacionArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), proyecto, asesorId, List.of(estudiante));
        asesorVigente = AsesorDomain.reconstruir(
                asesorId, "20161020123", "Ana Perez", "ana@uco.edu.co", UtilFecha.generarInstanteActual(), null);
        vinculoVigente = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), asesorId, proyecto, UtilFecha.generarInstanteActual(), null);
        cupoValido = new CupoEstudiantesArtefacto(List.of(estudiante), List.of(), List.of(estudiante));
        var versionRepositorioId = UtilUUID.generarNuevoUUID();
        var artefacto = ArtefactoDomain.reconstruir(
                reactivacion.getArtefacto(), proyecto, versionRepositorioId, asesorId, UtilFecha.generarInstanteActual());
        var versionRepositorio = VersionRepositorioArtefactoDomain.reconstruir(versionRepositorioId, "Formato A", 1,
                UtilUUID.generarNuevoUUID(), "Repo", new DatosArchivo("a.pdf", "application/pdf", 10L));
        peso = new PesoArtefactoReactivable(artefacto, versionRepositorio, 1024L);
        almacenamiento = new AlmacenamientoProyectoGrado(
                ProyectoGradoDomain.reconstruir(proyecto, "Proyecto"), 1_073_741_824L, 0L);
    }

    @Test
    void debePasar_cuandoTodasLasReglasSeCumplen() {
        // Act & Assert
        assertThatCode(() -> validator.validar(reactivacion, asesorVigente, List.of(vinculoVigente), cupoValido, peso, almacenamiento))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarAsesorNoEncontrado_antesQueLasDemasReglas_cuandoTodoFalla() {
        // Arrange
        var cupoExcedido = new CupoEstudiantesArtefacto(List.of(estudiante), List.of(), List.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(reactivacion, AsesorDomain.VACIO, List.of(), cupoExcedido, peso, almacenamiento))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoAsignado_antesQueElCupo_cuandoOtroTieneElVinculoMasReciente() {
        // Arrange
        var otroAsesor = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), proyecto,
                UtilFecha.generarInstanteActual(), null);
        var cupoExcedido = new CupoEstudiantesArtefacto(List.of(estudiante), List.of(), List.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(reactivacion, asesorVigente, List.of(otroAsesor), cupoExcedido, peso, almacenamiento))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }

    @Test
    void debeLanzarEstudiantesSuperanProyecto_cuandoElCupoExcedeAlProyecto() {
        // Arrange
        var vinculado = UtilUUID.generarNuevoUUID();
        var cupoExcedido = new CupoEstudiantesArtefacto(
                List.of(estudiante, vinculado), List.of(vinculado), List.of(vinculado));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(reactivacion, asesorVigente, List.of(vinculoVigente), cupoExcedido, peso, almacenamiento))
                .isInstanceOf(EstudiantesSuperanProyectoException.class);
    }

    @Test
    void debeLanzarCuotaExcedida_cuandoElPesoNoCabe() {
        // Arrange
        var sinEspacio = new AlmacenamientoProyectoGrado(
                almacenamiento.proyectoGrado(), 1_073_741_824L, 1_073_741_824L - 1023L);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                reactivacion, asesorVigente, List.of(vinculoVigente), cupoValido, peso, sinEspacio))
                .isInstanceOf(CuotaProyectoGradoExcedidaException.class);
    }

    @Test
    void debePasar_cuandoElPesoIgualElEspacioDisponible() {
        // Arrange
        var justo = new AlmacenamientoProyectoGrado(
                almacenamiento.proyectoGrado(), 1_073_741_824L, 1_073_741_824L - 1024L);

        // Act & Assert
        assertThatCode(() -> validator.validar(
                reactivacion, asesorVigente, List.of(vinculoVigente), cupoValido, peso, justo))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarArtefactoNoEncontrado_antesQueDocumentoProyectoYCuota() {
        // Arrange
        var sinNada = new PesoArtefactoReactivable(
                ArtefactoDomain.VACIO, VersionRepositorioArtefactoDomain.VACIO, 1024L);
        var sinProyecto = new AlmacenamientoProyectoGrado(ProyectoGradoDomain.VACIO, 1_073_741_824L, 1_073_741_824L);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                reactivacion, asesorVigente, List.of(vinculoVigente), cupoValido, sinNada, sinProyecto))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzarDocumentoNoEncontrado_antesQueProyectoYCuota() {
        // Arrange
        var sinDocumento = new PesoArtefactoReactivable(
                peso.artefacto(), VersionRepositorioArtefactoDomain.VACIO, 1024L);
        var sinProyecto = new AlmacenamientoProyectoGrado(ProyectoGradoDomain.VACIO, 1_073_741_824L, 1_073_741_824L);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                reactivacion, asesorVigente, List.of(vinculoVigente), cupoValido, sinDocumento, sinProyecto))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzarProyectoNoEncontrado_antesQueLaCuota() {
        // Arrange
        var sinProyecto = new AlmacenamientoProyectoGrado(ProyectoGradoDomain.VACIO, 1_073_741_824L, 1_073_741_824L);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                reactivacion, asesorVigente, List.of(vinculoVigente), cupoValido, peso, sinProyecto))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }
}
