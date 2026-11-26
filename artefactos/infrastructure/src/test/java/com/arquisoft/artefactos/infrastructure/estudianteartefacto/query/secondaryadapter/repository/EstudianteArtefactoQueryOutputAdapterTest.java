package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.ParticipacionArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.mapper.ConsultarParticipacionesArtefactoMapper;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarParticipacionesArtefactoQuery;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.entity.EstudianteArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.support.EstudianteTestEntity;
import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.ArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstudianteArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstadoRevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.RevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.VersionArtefactoTestEntity;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
        adapter = new EstudianteArtefactoQueryOutputAdapter(
                repository, participacionRepository, new ParticipacionArtefactoJpaSpecification());
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
        entityManager.persist(new ProyectoGradoJpaEntity(id, nombre, null, "EN_PROCESO", null));
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

    private void sembrarEstado(String id, String nombre) {
        entityManager.persist(new EstadoRevisionAsesorTestEntity(id, nombre));
    }

    private void sembrarRevision(UUID artefacto, String estadoId, long diasAtras) {
        var version = UtilUUID.generarNuevoUUID();
        var fecha = Instant.now().minus(diasAtras, ChronoUnit.DAYS);
        entityManager.persist(new VersionArtefactoTestEntity(version, artefacto, 1, fecha, "artefactos/" + version));
        entityManager.persist(new RevisionAsesorTestEntity(UtilUUID.generarNuevoUUID(), version, estadoId, fecha));
    }

    private UUID sembrarProyectoConEstado(String nombre, String estado) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(id, nombre, null, estado, Instant.now()));
        return id;
    }

    private static ParticipacionArtefactoCriteria criteria(
            UUID estudiante, NodoFiltro raiz, List<SortOrder> orden, int pagina, int tamanio) {
        var criterio = ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz);
        return ConsultarParticipacionesArtefactoMapper.toCriteria(
                ConsultarParticipacionesArtefactoQuery.crear(estudiante, criterio));
    }

    private static ParticipacionArtefactoCriteria criteria(UUID estudiante, NodoFiltro raiz) {
        return criteria(estudiante, raiz, List.of(), 0, 10);
    }

    private static List<UUID> ids(PaginatedResult<ParticipacionArtefactoReadModel> pagina) {
        return pagina.getContent().stream().map(ParticipacionArtefactoReadModel::id).toList();
    }

    @Test
    void debeRetornarSoloLasParticipacionesDelEstudiante_cuandoOtrosTambienTienenVinculos() {
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
        var resultado = adapter.consultarParticipaciones(criteria(estudiante, null));

        // Assert
        assertThat(ids(resultado)).containsExactly(propio);
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void debeIgnorarEstudianteIdDelCliente_cuandoPasaPorElMapperDeApplication() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        var proyecto = sembrarProyecto("Alfa");
        var version = sembrarVersion("Informe", 1, "Repo");
        var propio = sembrarParticipacion(estudiante, proyecto, version);
        var ajeno = sembrarParticipacion(otro, proyecto, version);
        entityManager.flush();
        entityManager.clear();
        var filtroAjeno = NodoFiltro.predicado("estudianteId", FiltroOperador.ES, otro.toString());

        // Act
        var resultado = adapter.consultarParticipaciones(criteria(estudiante, filtroAjeno));

        // Assert
        assertThat(ids(resultado)).doesNotContain(ajeno, propio);
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeIncluirOmitidos_cuandoElArtefactoFueOmitido() {
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
        var resultado = adapter.consultarParticipaciones(criteria(estudiante, null));

        // Assert
        assertThat(resultado.getContent()).filteredOn(ParticipacionArtefactoReadModel::omitido)
                .extracting("id").containsExactly(omitido);
        assertThat(resultado.getContent()).filteredOn(participacion -> !participacion.omitido())
                .extracting("id").containsExactly(activo);
    }

    @Test
    void debeOrdenarPorProyectoYDocumento_porDefecto() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var alfa = sembrarProyecto("Alfa");
        var beta = sembrarProyecto("Beta");
        var betaDoc1 = sembrarParticipacion(estudiante, beta, sembrarVersion("Anteproyecto", 1, "Repo"));
        var alfaInforme = sembrarParticipacion(estudiante, alfa, sembrarVersion("Informe", 2, "Repo"));
        var alfaAnteproyecto = sembrarParticipacion(estudiante, alfa, sembrarVersion("Anteproyecto", 5, "Repo"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarParticipaciones(criteria(estudiante, null));

        // Assert
        assertThat(ids(resultado)).containsExactly(alfaAnteproyecto, alfaInforme, betaDoc1);
    }

    @Test
    void debeDesempatarPorId_cuandoPaginaConValoresIguales() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = sembrarProyecto("Alfa");
        var version = sembrarVersion("Informe", 1, "Repo");
        var asesor = sembrarAsesor("Ana");
        var primero = sembrarParticipacion(estudiante, proyecto, version, asesor);
        var segundo = sembrarParticipacion(estudiante, proyecto, version, asesor);
        var tercero = sembrarParticipacion(estudiante, proyecto, version, asesor);
        entityManager.flush();
        entityManager.clear();
        var orden = List.of(SortOrder.of("nombreProyecto", SortDirection.ASC));

        // Act
        var pagina0 = adapter.consultarParticipaciones(criteria(estudiante, null, orden, 0, 1));
        var pagina1 = adapter.consultarParticipaciones(criteria(estudiante, null, orden, 1, 1));
        var pagina2 = adapter.consultarParticipaciones(criteria(estudiante, null, orden, 2, 1));

        // Assert
        var vistos = List.of(ids(pagina0).get(0), ids(pagina1).get(0), ids(pagina2).get(0));
        assertThat(vistos).containsExactlyInAnyOrder(primero, segundo, tercero);
        assertThat(pagina0.getTotalElements()).isEqualTo(3L);
    }

    @Test
    void debeIncluirNombresYAsesorDeCadaArtefacto_cuandoUneLasTablas() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = sembrarProyecto("Proyecto Alfa");
        var version = sembrarVersion("Informe", 3, "Repo Uno");
        var asesor = sembrarAsesor("Carla");
        var artefacto = sembrarParticipacion(estudiante, proyecto, version, asesor);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarParticipaciones(criteria(estudiante, null));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        var readModel = resultado.getContent().get(0);
        assertThat(readModel.id()).isEqualTo(artefacto);
        assertThat(readModel.proyectoGrado()).isEqualTo(proyecto);
        assertThat(readModel.nombreProyecto()).isEqualTo("Proyecto Alfa");
        assertThat(readModel.versionRepositorioArtefacto()).isEqualTo(version);
        assertThat(readModel.nombreDocumento()).isEqualTo("Informe");
        assertThat(readModel.versionDocumento()).isEqualTo(3);
        assertThat(readModel.nombreRepositorio()).isEqualTo("Repo Uno");
        assertThat(readModel.asesor()).isEqualTo(asesor);
        assertThat(readModel.nombreAsesor()).isEqualTo("Carla");
        assertThat(readModel.estadoUltimaRevision()).isNull();
        assertThat(readModel.proyectoFinalizado()).isFalse();
        assertThat(readModel.proyectoCancelado()).isFalse();
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElEstudianteNoTieneVinculos() {
        // Arrange
        var proyecto = sembrarProyecto("Alfa");
        var version = sembrarVersion("Informe", 1, "Repo");
        sembrarParticipacion(UtilUUID.generarNuevoUUID(), proyecto, version);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarParticipaciones(criteria(UtilUUID.generarNuevoUUID(), null));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
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
