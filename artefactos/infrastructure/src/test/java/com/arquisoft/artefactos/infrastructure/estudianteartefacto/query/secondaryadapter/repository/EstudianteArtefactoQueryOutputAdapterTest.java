package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.entity.EstudianteArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.support.EstudianteTestEntity;
import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.ArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstudianteArtefactoTestEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstudianteArtefactoQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstudianteArtefactoQueryRepository repository;

    @Autowired
    private ParticipacionArtefactoQueryRepository participacionRepository;

    private EstudianteArtefactoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteArtefactoQueryOutputAdapter(repository, participacionRepository);
    }

    private UUID sembrarArtefacto() {
        var artefacto = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoTestEntity(artefacto));
        return artefacto;
    }

    private UUID vincular(UUID artefacto, String nombre, Instant eliminadoEn) {
        return vincular(UtilUUID.generarNuevoUUID(), artefacto, nombre, eliminadoEn);
    }

    private UUID vincular(UUID vinculo, UUID artefacto, String nombre, Instant eliminadoEn) {
        var estudiante = UtilUUID.generarNuevoUUID();
        entityManager.persist(new EstudianteTestEntity(
                estudiante, "ID-" + nombre, nombre, nombre.toLowerCase() + "@uco.edu.co", eliminadoEn));
        entityManager.persist(new EstudianteArtefactoTestEntity(vinculo, artefacto, estudiante));
        return estudiante;
    }

    private UUID sembrarProyecto(String nombre) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(id, nombre));
        return id;
    }

    private UUID sembrarVersion(String documento, int version, String repositorio) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionRepositorioArtefactoJpaEntity(
                id, documento, version, UtilUUID.generarNuevoUUID(), repositorio,
                "archivo.pdf", "application/pdf", 10L));
        return id;
    }

    private UUID sembrarAsesor(String nombre) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new AsesorJpaEntity(id, "ID-" + nombre, nombre, nombre.toLowerCase() + "@uco.edu.co",
                Instant.now(), null));
        return id;
    }

    private UUID sembrarParticipacion(UUID estudiante, UUID proyecto, UUID version) {
        return sembrarParticipacion(estudiante, proyecto, version, sembrarAsesor("Ana"));
    }

    private UUID sembrarParticipacion(UUID estudiante, UUID proyecto, UUID version, UUID asesor) {
        var artefacto = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoJpaEntity(artefacto, proyecto, version, asesor, null));
        entityManager.persist(new EstudianteArtefactoJpaEntity(UtilUUID.generarNuevoUUID(), artefacto, estudiante));
        return artefacto;
    }

    @Test
    void debeRetornarSoloLosArtefactosDelEstudiante_cuandoOtrosEstudiantesTambienTienenVinculos() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        var proyecto = sembrarProyecto("Alfa");
        var version = sembrarVersion("Informe", 1, "Repo");
        var propio = sembrarParticipacion(estudiante, proyecto, version);
        sembrarParticipacion(otro, proyecto, version);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarParticipacionesPorEstudiante(estudiante);

        // Assert
        assertThat(resultado).extracting("id").containsExactly(propio);
    }

    @Test
    void debeMarcarOmitidoEnLasParticipaciones_cuandoElArtefactoFueOmitido() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = sembrarProyecto("Alfa");
        var version = sembrarVersion("Informe", 1, "Repo");
        var activo = sembrarParticipacion(estudiante, proyecto, version);
        var omitido = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoJpaEntity(
                omitido, proyecto, version, sembrarAsesor("Beto"), Instant.now()));
        entityManager.persist(new EstudianteArtefactoJpaEntity(UtilUUID.generarNuevoUUID(), omitido, estudiante));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarParticipacionesPorEstudiante(estudiante);

        // Assert
        assertThat(resultado).filteredOn(participacion -> participacion.omitido())
                .extracting("id").containsExactly(omitido);
        assertThat(resultado).filteredOn(participacion -> !participacion.omitido())
                .extracting("id").containsExactly(activo);
    }

    @Test
    void debeOrdenarPorProyectoDocumentoYVersion_cuandoHayVarios() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var alfa = sembrarProyecto("Alfa");
        var beta = sembrarProyecto("Beta");
        var betaDoc1 = sembrarParticipacion(estudiante, beta, sembrarVersion("Anteproyecto", 1, "Repo"));
        var alfaInformeV2 = sembrarParticipacion(estudiante, alfa, sembrarVersion("Informe", 2, "Repo"));
        var alfaInformeV1 = sembrarParticipacion(estudiante, alfa, sembrarVersion("Informe", 1, "Repo"));
        var alfaAnteproyecto = sembrarParticipacion(estudiante, alfa, sembrarVersion("Anteproyecto", 5, "Repo"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarParticipacionesPorEstudiante(estudiante);

        // Assert
        assertThat(resultado).extracting("id")
                .containsExactly(alfaAnteproyecto, alfaInformeV1, alfaInformeV2, betaDoc1);
    }

    @Test
    void debeIncluirNombresDeProyectoYRepositorio_cuandoUneLasTablas() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = sembrarProyecto("Proyecto Alfa");
        var version = sembrarVersion("Informe", 3, "Repo Uno");
        var asesor = sembrarAsesor("Carla");
        var artefacto = sembrarParticipacion(estudiante, proyecto, version, asesor);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarParticipacionesPorEstudiante(estudiante);

        // Assert
        assertThat(resultado).hasSize(1);
        var readModel = resultado.get(0);
        assertThat(readModel.id()).isEqualTo(artefacto);
        assertThat(readModel.proyectoGrado()).isEqualTo(proyecto);
        assertThat(readModel.nombreProyecto()).isEqualTo("Proyecto Alfa");
        assertThat(readModel.versionRepositorioArtefacto()).isEqualTo(version);
        assertThat(readModel.nombreDocumento()).isEqualTo("Informe");
        assertThat(readModel.versionDocumento()).isEqualTo(3);
        assertThat(readModel.nombreRepositorio()).isEqualTo("Repo Uno");
        assertThat(readModel.asesor()).isEqualTo(asesor);
        assertThat(readModel.nombreAsesor()).isEqualTo("Carla");
    }

    @Test
    void debeIncluirElAsesorDeCadaArtefacto_cuandoLosArtefactosTienenAsesoresDistintos() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = sembrarProyecto("Alfa");
        var asesorUno = sembrarAsesor("Diana");
        var asesorDos = sembrarAsesor("Elena");
        var primero = sembrarParticipacion(estudiante, proyecto, sembrarVersion("Anteproyecto", 1, "Repo"), asesorUno);
        var segundo = sembrarParticipacion(estudiante, proyecto, sembrarVersion("Informe", 1, "Repo"), asesorDos);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarParticipacionesPorEstudiante(estudiante);

        // Assert
        assertThat(resultado).extracting("id").containsExactly(primero, segundo);
        assertThat(resultado).extracting("asesor").containsExactly(asesorUno, asesorDos);
        assertThat(resultado).extracting("nombreAsesor").containsExactly("Diana", "Elena");
    }

    @Test
    void debeRetornarListaVacia_cuandoElEstudianteNoTieneVinculos() {
        // Arrange
        var proyecto = sembrarProyecto("Alfa");
        var version = sembrarVersion("Informe", 1, "Repo");
        sembrarParticipacion(UtilUUID.generarNuevoUUID(), proyecto, version);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarParticipacionesPorEstudiante(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarOrdenadosPorNombreYId_cuandoHayVarios() {
        // Arrange
        var artefacto = sembrarArtefacto();
        vincular(artefacto, "Zoe", null);
        vincular(artefacto, "Ana", null);
        vincular(artefacto, "Luis", null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorArtefacto(artefacto);

        // Assert
        assertThat(resultado).extracting("nombre").containsExactly("Ana", "Luis", "Zoe");
    }

    @Test
    void debeDesempatarPorId_cuandoDosEstudiantesTienenElMismoNombre() {
        // Arrange
        var artefacto = sembrarArtefacto();
        var vinculoMenor = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var vinculoMayor = UUID.fromString("00000000-0000-0000-0000-000000000002");
        var estudianteDelMayor = vincular(vinculoMayor, artefacto, "Ana", null);
        var estudianteDelMenor = vincular(vinculoMenor, artefacto, "Ana", null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorArtefacto(artefacto);

        // Assert
        assertThat(resultado).extracting("id").containsExactly(vinculoMenor, vinculoMayor);
        assertThat(resultado).extracting("estudianteId").containsExactly(estudianteDelMenor, estudianteDelMayor);
    }

    @Test
    void debeIncluirBajaConVigenteFalse_cuandoEstudianteTieneEliminadoEn() {
        // Arrange
        var artefacto = sembrarArtefacto();
        var baja = vincular(artefacto, "Ana", Instant.parse("2026-01-01T00:00:00Z"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorArtefacto(artefacto);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).estudianteId()).isEqualTo(baja);
        assertThat(resultado.get(0).vigente()).isFalse();
    }

    @Test
    void debeMarcarVigenteTrue_cuandoNoTieneEliminadoEn() {
        // Arrange
        var artefacto = sembrarArtefacto();
        var vigente = vincular(artefacto, "Ana", null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorArtefacto(artefacto);

        // Assert
        assertThat(resultado).hasSize(1);
        var readModel = resultado.get(0);
        assertThat(readModel.vigente()).isTrue();
        assertThat(readModel.artefactoId()).isEqualTo(artefacto);
        assertThat(readModel.estudianteId()).isEqualTo(vigente);
        assertThat(readModel.identificador()).isEqualTo("ID-Ana");
        assertThat(readModel.nombre()).isEqualTo("Ana");
        assertThat(readModel.email()).isEqualTo("ana@uco.edu.co");
    }

    @Test
    void debeExcluirVinculosDeOtroArtefacto_cuandoConsulta() {
        // Arrange
        var artefacto = sembrarArtefacto();
        var otro = sembrarArtefacto();
        var propio = vincular(artefacto, "Ana", null);
        vincular(otro, "Luis", null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorArtefacto(artefacto);

        // Assert
        assertThat(resultado).extracting("estudianteId").containsExactly(propio);
    }

    @Test
    void debeRetornarVacio_cuandoArtefactoSinVinculosOInexistente() {
        // Arrange
        var sinVinculos = sembrarArtefacto();
        entityManager.flush();

        // Act
        var deSinVinculos = adapter.consultarPorArtefacto(sinVinculos);
        var deInexistente = adapter.consultarPorArtefacto(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(deSinVinculos).isEmpty();
        assertThat(deInexistente).isEmpty();
    }
}
