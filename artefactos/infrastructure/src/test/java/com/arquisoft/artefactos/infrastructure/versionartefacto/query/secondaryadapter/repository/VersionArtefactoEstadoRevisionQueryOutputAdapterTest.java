package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoAsesorCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoEstudianteCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.mapper.ConsultarVersionesArtefactoAsesorMapper;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.mapper.ConsultarVersionesArtefactoEstudianteMapper;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionesArtefactoAsesorQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionesArtefactoEstudianteQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstadoRevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstudianteArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.RevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.VersionArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VersionArtefactoEstadoRevisionQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VersionArtefactoAsesorQueryRepository repository;

    @Autowired
    private VersionArtefactoEstudianteQueryRepository estudianteRepository;

    private VersionArtefactoQueryOutputAdapter adapter;

    private final UUID asesorA = UtilUUID.generarNuevoUUID();
    private final UUID asesorB = UtilUUID.generarNuevoUUID();
    private final UUID estudianteX = UtilUUID.generarNuevoUUID();
    private final UUID estudianteY = UtilUUID.generarNuevoUUID();

    private UUID proyectoAlfa;
    private UUID proyectoBeta;
    private UUID artefactoA1;
    private UUID artefactoA2;
    private UUID artefactoB;
    private UUID versionA1v1;
    private UUID versionA1v2;
    private UUID versionA2v1;
    private UUID versionB;

    @BeforeEach
    void setUp() {
        adapter = new VersionArtefactoQueryOutputAdapter(
                repository, new VersionArtefactoAsesorJpaSpecification(),
                estudianteRepository, new VersionArtefactoEstudianteJpaSpecification());
    }

    private UUID sembrarVersion(UUID artefacto, int numero, String fecha) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionArtefactoTestEntity(
                id, artefacto, numero, Instant.parse(fecha), "artefactos/" + id + ".pdf"));
        return id;
    }

    private UUID sembrarProyecto(String nombre) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(id, nombre));
        return id;
    }

    private UUID sembrarVersionRepositorio(String documento, int version, String repositorio) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionRepositorioArtefactoJpaEntity(
                id, documento, version, UtilUUID.generarNuevoUUID(), repositorio,
                "archivo.pdf", "application/pdf", 10L));
        return id;
    }

    private UUID sembrarArtefacto(UUID proyecto, UUID versionRepositorio, UUID asesor) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoJpaEntity(id, proyecto, versionRepositorio, asesor, null));
        return id;
    }

    private void sembrarEscenario() {
        proyectoAlfa = sembrarProyecto("Alfa");
        proyectoBeta = sembrarProyecto("Beta");
        entityManager.persist(new AsesorJpaEntity(asesorA, "ID-Ana", "Ana", "ana@uco.edu.co", Instant.now(), null));
        entityManager.persist(new AsesorJpaEntity(
                asesorB, "ID-Beto", "Beto", "beto@uco.edu.co", Instant.now(), Instant.now()));
        var docAnteproyecto = sembrarVersionRepositorio("Anteproyecto", 1, "Repo Uno");
        var docInforme = sembrarVersionRepositorio("Informe", 2, "Repo Dos");
        artefactoA1 = sembrarArtefacto(proyectoAlfa, docAnteproyecto, asesorA);
        artefactoA2 = sembrarArtefacto(proyectoBeta, docInforme, asesorA);
        artefactoB = sembrarArtefacto(proyectoAlfa, docAnteproyecto, asesorB);

        versionA1v1 = sembrarVersion(artefactoA1, 1, "2026-09-01T10:00:00Z");
        versionA1v2 = sembrarVersion(artefactoA1, 2, "2026-10-20T10:00:00Z");
        versionA2v1 = sembrarVersion(artefactoA2, 1, "2026-10-10T10:00:00Z");
        versionB = sembrarVersion(artefactoB, 1, "2026-10-25T10:00:00Z");
        entityManager.flush();
        entityManager.clear();
    }

    private VersionArtefactoAsesorCriteria criteria(
            UUID asesor, NodoFiltro raiz, List<SortOrder> orden, int pagina, int tamanio) {
        var criterio = ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz);
        return ConsultarVersionesArtefactoAsesorMapper.toCriteria(
                ConsultarVersionesArtefactoAsesorQuery.crear(asesor, criterio));
    }

    private VersionArtefactoAsesorCriteria criteria(UUID asesor, NodoFiltro raiz) {
        return criteria(asesor, raiz, List.of(), 0, 10);
    }

    private static List<UUID> ids(List<VersionArtefactoReadModel> contenido) {
        return contenido.stream().map(VersionArtefactoReadModel::id).toList();
    }

    private void sembrarEscenarioEstudiante() {
        sembrarEscenario();
        entityManager.persist(new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), artefactoA1, estudianteX));
        entityManager.persist(new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), artefactoA2, estudianteX));
        entityManager.persist(new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), artefactoA1, estudianteY));
        entityManager.persist(new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), artefactoB, estudianteY));
        entityManager.flush();
        entityManager.clear();
    }

    private VersionArtefactoEstudianteCriteria criteriaEstudiante(
            UUID estudiante, NodoFiltro raiz, List<SortOrder> orden, int pagina, int tamanio) {
        var criterio = ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz);
        return ConsultarVersionesArtefactoEstudianteMapper.toCriteria(
                ConsultarVersionesArtefactoEstudianteQuery.crear(estudiante, criterio));
    }

    private VersionArtefactoEstudianteCriteria criteriaEstudiante(UUID estudiante, NodoFiltro raiz) {
        return criteriaEstudiante(estudiante, raiz, List.of(), 0, 10);
    }


    private static final UUID REVISION_B_MENOR = UtilUUID.generarUUIDDesdeTexto("00000000-0000-0000-0000-000000000001");
    private static final UUID REVISION_B_MAYOR = UtilUUID.generarUUIDDesdeTexto("00000000-0000-0000-0000-000000000002");

    private UUID revisionA1v2Reciente;

    private void sembrarRevision(UUID id, UUID version, String estado, String fecha) {
        entityManager.persist(new RevisionAsesorTestEntity(id, version, estado, Instant.parse(fecha)));
    }

    private void sembrarEscenarioConRevisiones() {
        sembrarEscenarioEstudiante();
        entityManager.persist(new EstadoRevisionAsesorTestEntity("PENDIENTE", "Pendiente", 1));
        entityManager.persist(new EstadoRevisionAsesorTestEntity("EN_PROGRESO", "En progreso", 2));
        entityManager.persist(new EstadoRevisionAsesorTestEntity("RESUELTA", "Resuelta", 3));
        entityManager.persist(new EstadoRevisionAsesorTestEntity("CERRADA", "Cerrada", 4));
        revisionA1v2Reciente = UtilUUID.generarNuevoUUID();
        sembrarRevision(UtilUUID.generarNuevoUUID(), versionA1v2, "PENDIENTE", "2026-10-21T10:00:00Z");
        sembrarRevision(revisionA1v2Reciente, versionA1v2, "RESUELTA", "2026-10-22T10:00:00Z");
        sembrarRevision(UtilUUID.generarNuevoUUID(), versionA2v1, "EN_PROGRESO", "2026-10-21T10:00:00Z");
        sembrarRevision(REVISION_B_MENOR, versionB, "CERRADA", "2026-10-23T10:00:00Z");
        sembrarRevision(REVISION_B_MAYOR, versionB, "PENDIENTE", "2026-10-23T10:00:00Z");
    }

    private void confirmarSiembra() {
        entityManager.flush();
        entityManager.clear();
    }

    private static NodoFiltro estadoRevision(FiltroOperador operador, String valor) {
        return NodoFiltro.predicado("estadoRevision", operador, valor);
    }

    private static NodoFiltro estadoRevision(FiltroOperador operador, List<String> valores) {
        return NodoFiltro.predicadoMultivalor("estadoRevision", operador, valores);
    }

    private VersionArtefactoReadModel buscar(List<VersionArtefactoReadModel> contenido, UUID id) {
        return contenido.stream().filter(v -> v.id().equals(id)).findFirst().orElseThrow();
    }

    @Test
    void debeRetornarEstadoRevisionNulo_cuandoLaVersionNoTieneRevision() {
        // Arrange
        sembrarEscenarioConRevisiones();
        confirmarSiembra();

        // Act
        var comoAsesor = adapter.consultarTodasAsesor(criteria(asesorA, null));
        var comoEstudiante = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, null));

        // Assert
        assertThat(comoAsesor.getTotalElements()).isEqualTo(3L);
        assertThat(comoEstudiante.getTotalElements()).isEqualTo(3L);
        var sinRevisionAsesor = buscar(comoAsesor.getContent(), versionA1v1);
        var sinRevisionEstudiante = buscar(comoEstudiante.getContent(), versionA1v1);
        assertThat(sinRevisionAsesor.revisionAsesor()).isNull();
        assertThat(sinRevisionAsesor.estadoRevision()).isNull();
        assertThat(sinRevisionAsesor.nombreEstadoRevision()).isNull();
        assertThat(sinRevisionEstudiante.revisionAsesor()).isNull();
        assertThat(sinRevisionEstudiante.estadoRevision()).isNull();
        assertThat(sinRevisionEstudiante.nombreEstadoRevision()).isNull();
    }

    @Test
    void debeRetornarLosDatosDeLaRevisionMasReciente_cuandoLaVersionTieneVarias() {
        // Arrange
        sembrarEscenarioConRevisiones();
        confirmarSiembra();

        // Act
        var comoAsesor = adapter.consultarTodasAsesor(criteria(asesorA, null));
        var comoEstudiante = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, null));

        // Assert
        assertThat(comoAsesor.getContent()).hasSize(3);
        assertThat(ids(comoAsesor.getContent())).containsOnlyOnce(versionA1v2);
        var conVarias = buscar(comoAsesor.getContent(), versionA1v2);
        assertThat(conVarias.revisionAsesor()).isEqualTo(revisionA1v2Reciente);
        assertThat(conVarias.estadoRevision()).isEqualTo("RESUELTA");
        assertThat(conVarias.nombreEstadoRevision()).isEqualTo("Resuelta");
        assertThat(comoEstudiante.getContent()).hasSize(3);
        assertThat(buscar(comoEstudiante.getContent(), versionA1v2).revisionAsesor())
                .isEqualTo(revisionA1v2Reciente);
        assertThat(buscar(comoAsesor.getContent(), versionA2v1).estadoRevision()).isEqualTo("EN_PROGRESO");
    }

    @Test
    void debeDesempatarPorId_cuandoDosRevisionesTienenLaMismaFecha() {
        // Arrange
        sembrarEscenarioConRevisiones();
        confirmarSiembra();

        // Act
        var resultado = adapter.consultarTodasAsesor(criteria(asesorB, null));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        var version = resultado.getContent().get(0);
        assertThat(version.revisionAsesor()).isEqualTo(REVISION_B_MAYOR);
        assertThat(version.estadoRevision()).isEqualTo("PENDIENTE");
    }

    @Test
    void debeFiltrarPorEstadoRevision_cuandoUsaEsONoEs() {
        // Arrange
        sembrarEscenarioConRevisiones();
        confirmarSiembra();

        // Act
        var esAsesor = adapter.consultarTodasAsesor(criteria(asesorA, estadoRevision(FiltroOperador.ES, "RESUELTA")));
        var esEstudiante = adapter.consultarTodasEstudiante(
                criteriaEstudiante(estudianteX, estadoRevision(FiltroOperador.ES, "RESUELTA")));
        var noEs = adapter.consultarTodasAsesor(criteria(asesorA, estadoRevision(FiltroOperador.NO_ES, "RESUELTA")));

        // Assert
        assertThat(ids(esAsesor.getContent())).containsExactly(versionA1v2);
        assertThat(ids(esEstudiante.getContent())).containsExactly(versionA1v2);
        assertThat(ids(noEs.getContent())).containsExactly(versionA2v1);
    }

    @Test
    void debeFiltrarPorEstadoRevision_cuandoUsaInONotIn() {
        // Arrange
        sembrarEscenarioConRevisiones();
        confirmarSiembra();
        var pendienteOEnProgreso = List.of("PENDIENTE", "EN_PROGRESO");

        // Act
        var enAsesor = adapter.consultarTodasAsesor(
                criteria(asesorA, estadoRevision(FiltroOperador.IN, pendienteOEnProgreso)));
        var enEstudiante = adapter.consultarTodasEstudiante(
                criteriaEstudiante(estudianteY, estadoRevision(FiltroOperador.IN, pendienteOEnProgreso)));
        var fueraDeLista = adapter.consultarTodasAsesor(
                criteria(asesorA, estadoRevision(FiltroOperador.NOT_IN, pendienteOEnProgreso)));

        // Assert
        assertThat(ids(enAsesor.getContent())).containsExactly(versionA2v1);
        assertThat(ids(enEstudiante.getContent())).containsExactly(versionB);
        assertThat(ids(fueraDeLista.getContent())).containsExactly(versionA1v2);
    }

    @Test
    void debeRetornarSoloVersionesSinRevision_cuandoUsaEsNulo() {
        // Arrange
        sembrarEscenarioConRevisiones();
        confirmarSiembra();
        var esNulo = estadoRevision(FiltroOperador.ES_NULO, (String) null);
        var noEsNulo = estadoRevision(FiltroOperador.NO_ES_NULO, (String) null);

        // Act
        var sinRevision = adapter.consultarTodasAsesor(criteria(asesorA, esNulo));
        var conRevision = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, noEsNulo));

        // Assert
        assertThat(ids(sinRevision.getContent())).containsExactly(versionA1v1);
        assertThat(ids(conRevision.getContent())).containsExactlyInAnyOrder(versionA1v2, versionA2v1);
    }

    @Test
    void debeIncluirSinRevisionYPendientes_cuandoUneEsNuloConEsPendientePorO() {
        // Arrange
        sembrarEscenarioConRevisiones();
        sembrarRevision(UtilUUID.generarNuevoUUID(), versionA2v1, "PENDIENTE", "2026-10-25T10:00:00Z");
        confirmarSiembra();
        var raiz = NodoFiltro.grupo(FiltroConector.OR, List.of(
                estadoRevision(FiltroOperador.ES_NULO, (String) null),
                estadoRevision(FiltroOperador.ES, "PENDIENTE")));

        // Act
        var resultado = adapter.consultarTodasAsesor(criteria(asesorA, raiz));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactlyInAnyOrder(versionA1v1, versionA2v1);
    }

    @Test
    void debeOrdenarPorFlujoDelEstado_cuandoOrdenaPorEstadoRevision() {
        // Arrange
        sembrarEscenarioConRevisiones();
        sembrarRevision(UtilUUID.generarNuevoUUID(), versionA1v1, "PENDIENTE", "2026-10-25T10:00:00Z");
        confirmarSiembra();
        var conRevision = estadoRevision(FiltroOperador.NO_ES_NULO, (String) null);
        var ordenAscendente = List.of(SortOrder.of("estadoRevision", SortDirection.ASC));
        var ordenDescendente = List.of(SortOrder.of("estadoRevision", SortDirection.DESC));

        // Act
        var ascendente = adapter.consultarTodasAsesor(criteria(asesorA, conRevision, ordenAscendente, 0, 10));
        var descendente = adapter.consultarTodasEstudiante(
                criteriaEstudiante(estudianteX, conRevision, ordenDescendente, 0, 10));

        // Assert
        assertThat(ids(ascendente.getContent())).containsExactly(versionA1v1, versionA2v1, versionA1v2);
        assertThat(ids(descendente.getContent())).containsExactly(versionA1v2, versionA2v1, versionA1v1);
    }

    @Test
    void debePaginarSinRepetirFilas_cuandoOrdenaPorEstadoConEmpates() {
        // Arrange
        sembrarEscenarioConRevisiones();
        sembrarRevision(UtilUUID.generarNuevoUUID(), versionA1v1, "EN_PROGRESO", "2026-10-25T10:00:00Z");
        sembrarRevision(UtilUUID.generarNuevoUUID(), versionA1v2, "EN_PROGRESO", "2026-10-26T10:00:00Z");
        confirmarSiembra();
        var conRevision = estadoRevision(FiltroOperador.NO_ES_NULO, (String) null);
        var orden = List.of(SortOrder.of("estadoRevision", SortDirection.ASC));

        // Act
        var vistas = new ArrayList<UUID>();
        for (var pagina = 0; pagina < 3; pagina++) {
            var contenido = adapter.consultarTodasAsesor(criteria(asesorA, conRevision, orden, pagina, 1)).getContent();
            vistas.addAll(ids(contenido));
        }

        // Assert
        assertThat(vistas).containsExactlyInAnyOrder(versionA1v1, versionA1v2, versionA2v1).doesNotHaveDuplicates();
    }

    @Test
    void debeNoMostrarRevisionesDeVersionesAjenas_cuandoFiltraPorEstadoRevision() {
        // Arrange
        sembrarEscenarioConRevisiones();
        confirmarSiembra();
        var pendiente = estadoRevision(FiltroOperador.ES, "PENDIENTE");

        // Act
        var delAsesor = adapter.consultarTodasAsesor(criteria(asesorA, pendiente));
        var delEstudiante = adapter.consultarTodasEstudiante(criteriaEstudiante(estudianteX, pendiente));

        // Assert
        assertThat(delAsesor.getContent()).isEmpty();
        assertThat(delEstudiante.getContent()).isEmpty();
    }
}
