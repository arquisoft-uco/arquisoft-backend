package com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorAsesorCriteria;
import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.mapper.ConsultarRevisionesAsesorAsesorMapper;
import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.model.ConsultarRevisionesAsesorAsesorQuery;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorAsesorReadModel;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.ArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstadoRevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.RevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.VersionArtefactoTestEntity;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RevisionAsesorAsesorQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RevisionAsesorEstudianteQueryRepository estudianteRepository;

    @Autowired
    private RevisionAsesorAsesorQueryRepository repository;

    private RevisionAsesorQueryOutputAdapter adapter;

    private static final Instant FECHA_CREACION = Instant.parse("2026-10-01T10:00:00Z");

    private final UUID asesorA = UtilUUID.generarNuevoUUID();
    private final UUID asesorB = UtilUUID.generarNuevoUUID();

    private UUID artefactoA;
    private UUID versionA1;
    private UUID versionA2;
    private UUID versionB;
    private UUID revisionA1;
    private UUID revisionA2;
    private UUID revisionB;

    @BeforeEach
    void setUp() {
        adapter = new RevisionAsesorQueryOutputAdapter(estudianteRepository,
                new RevisionAsesorEstudianteJpaSpecification(),
                repository, new RevisionAsesorAsesorJpaSpecification());
    }

    private UUID sembrarVersion(UUID artefacto, int numero) {
        var version = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionArtefactoTestEntity(version, artefacto, numero));
        return version;
    }

    private UUID sembrarRevision(UUID version, String estado) {
        var revision = UtilUUID.generarNuevoUUID();
        entityManager.persist(new RevisionAsesorTestEntity(revision, version, estado, FECHA_CREACION));
        return revision;
    }

    private void sembrarEscenario() {
        entityManager.persist(new EstadoRevisionAsesorTestEntity("PENDIENTE", "Pendiente"));
        entityManager.persist(new EstadoRevisionAsesorTestEntity("EN_PROGRESO", "En progreso"));

        artefactoA = UtilUUID.generarNuevoUUID();
        var artefactoB = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoTestEntity(artefactoA, asesorA));
        entityManager.persist(new ArtefactoTestEntity(artefactoB, asesorB));

        versionA1 = sembrarVersion(artefactoA, 1);
        versionA2 = sembrarVersion(artefactoA, 2);
        versionB = sembrarVersion(artefactoB, 1);

        revisionA1 = sembrarRevision(versionA1, "PENDIENTE");
        revisionA2 = sembrarRevision(versionA2, "EN_PROGRESO");
        revisionB = sembrarRevision(versionB, "PENDIENTE");
        entityManager.flush();
        entityManager.clear();
    }

    private RevisionAsesorAsesorCriteria criteria(
            UUID asesor, NodoFiltro raiz, List<SortOrder> orden, int pagina, int tamanio) {
        var criterio = ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz);
        return ConsultarRevisionesAsesorAsesorMapper.toCriteria(
                ConsultarRevisionesAsesorAsesorQuery.crear(asesor, criterio));
    }

    private RevisionAsesorAsesorCriteria criteria(UUID asesor, NodoFiltro raiz) {
        return criteria(asesor, raiz, List.of(), 0, 10);
    }

    private static List<UUID> ids(List<RevisionAsesorAsesorReadModel> contenido) {
        return contenido.stream().map(RevisionAsesorAsesorReadModel::id).toList();
    }

    @Test
    void debeRetornarVacio_cuandoNoHayRevisionesEnBD() {
        // Arrange
        var criteria = criteria(asesorA, null);

        // Act
        var resultado = adapter.consultarTodasAsesor(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeRetornarSoloRevisionesDeArtefactosDelAsesor_unaFilaPorRevision() {
        // Arrange
        sembrarEscenario();

        // Act
        var deA = adapter.consultarTodasAsesor(criteria(asesorA, null));
        var deB = adapter.consultarTodasAsesor(criteria(asesorB, null));
        var deSinArtefactos = adapter.consultarTodasAsesor(criteria(UtilUUID.generarNuevoUUID(), null));

        // Assert
        assertThat(deA.getTotalElements()).isEqualTo(2L);
        assertThat(ids(deA.getContent())).containsExactlyInAnyOrder(revisionA1, revisionA2);
        assertThat(ids(deB.getContent())).containsExactly(revisionB);
        assertThat(deSinArtefactos.getContent()).isEmpty();
    }

    @Test
    void debeNoPermitirSustituirAlAsesorDelToken_cuandoElBodyFiltraPorOtroAsesorId() {
        // Arrange
        sembrarEscenario();
        var raizCliente = NodoFiltro.predicado("asesorId", FiltroOperador.ES, asesorB.toString());

        // Act
        var resultado = adapter.consultarTodasAsesor(criteria(asesorA, raizCliente));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeFiltrarPorVersionArtefacto_yExcluirVersionesDeOtroAsesor() {
        // Arrange
        sembrarEscenario();

        // Act
        var propia = adapter.consultarTodasAsesor(criteria(asesorA,
                NodoFiltro.predicado("versionArtefacto", FiltroOperador.ES, versionA2.toString())));
        var ajena = adapter.consultarTodasAsesor(criteria(asesorA,
                NodoFiltro.predicado("versionArtefacto", FiltroOperador.ES, versionB.toString())));

        // Assert
        assertThat(ids(propia.getContent())).containsExactly(revisionA2);
        assertThat(ajena.getContent()).isEmpty();
    }

    @Test
    void debeFiltrarPorEstadoRevisionAsesor_yRetornarVacioSiElEstadoNoExiste() {
        // Arrange
        sembrarEscenario();

        // Act
        var enProgreso = adapter.consultarTodasAsesor(criteria(asesorA,
                NodoFiltro.predicado("estadoRevisionAsesor", FiltroOperador.ES, "EN_PROGRESO")));
        var inexistente = adapter.consultarTodasAsesor(criteria(asesorA,
                NodoFiltro.predicado("estadoRevisionAsesor", FiltroOperador.ES, "NO_EXISTE")));

        // Assert
        assertThat(ids(enProgreso.getContent())).containsExactly(revisionA2);
        assertThat(inexistente.getContent()).isEmpty();
        assertThat(inexistente.getTotalElements()).isZero();
    }

    @Test
    void debeAplicarAnd_cuandoCombinaFiltroDeVersionYDeEstado() {
        // Arrange
        sembrarEscenario();
        var coincide = NodoFiltro.grupo(FiltroConector.AND, List.of(
                NodoFiltro.predicado("versionArtefacto", FiltroOperador.ES, versionA1.toString()),
                NodoFiltro.predicado("estadoRevisionAsesor", FiltroOperador.ES, "PENDIENTE")));
        var noCoincide = NodoFiltro.grupo(FiltroConector.AND, List.of(
                NodoFiltro.predicado("versionArtefacto", FiltroOperador.ES, versionA1.toString()),
                NodoFiltro.predicado("estadoRevisionAsesor", FiltroOperador.ES, "EN_PROGRESO")));

        // Act
        var conCoincidencia = adapter.consultarTodasAsesor(criteria(asesorA, coincide));
        var sinCoincidencia = adapter.consultarTodasAsesor(criteria(asesorA, noCoincide));

        // Assert
        assertThat(ids(conCoincidencia.getContent())).containsExactly(revisionA1);
        assertThat(sinCoincidencia.getContent()).isEmpty();
    }

    @Test
    void debeOrdenarPorNombreDelEstado_cuandoSePideAscendenteYDescendente() {
        // Arrange
        sembrarEscenario();
        var asc = List.of(SortOrder.of("estadoRevisionAsesor", SortDirection.ASC));
        var desc = List.of(SortOrder.of("estadoRevisionAsesor", SortDirection.DESC));

        // Act
        var ascendente = adapter.consultarTodasAsesor(criteria(asesorA, null, asc, 0, 10));
        var descendente = adapter.consultarTodasAsesor(criteria(asesorA, null, desc, 0, 10));

        // Assert
        assertThat(ascendente.getContent()).extracting(RevisionAsesorAsesorReadModel::estadoRevisionAsesor)
                .containsExactly("EN_PROGRESO", "PENDIENTE");
        assertThat(descendente.getContent()).extracting(RevisionAsesorAsesorReadModel::estadoRevisionAsesor)
                .containsExactly("PENDIENTE", "EN_PROGRESO");
    }

    @Test
    void debePaginar_cuandoHayMasRevisionesQueElTamanioDePagina() {
        // Arrange
        sembrarEscenario();
        var asc = List.of(SortOrder.of("estadoRevisionAsesor", SortDirection.ASC));

        // Act
        var primera = adapter.consultarTodasAsesor(criteria(asesorA, null, asc, 0, 1));
        var segunda = adapter.consultarTodasAsesor(criteria(asesorA, null, asc, 1, 1));

        // Assert
        assertThat(primera.getContent()).hasSize(1);
        assertThat(primera.getTotalElements()).isEqualTo(2L);
        assertThat(segunda.getContent()).hasSize(1);
        assertThat(segunda.getTotalElements()).isEqualTo(2L);
        assertThat(ids(primera.getContent())).doesNotContainAnyElementsOf(ids(segunda.getContent()));
    }

    @Test
    void debeProyectarArtefactoVersionEIdYNombreDelEstado_cuandoLaRevisionExiste() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("versionArtefacto", FiltroOperador.ES, versionA2.toString());

        // Act
        var resultado = adapter.consultarTodasAsesor(criteria(asesorA, raiz));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        var revision = resultado.getContent().get(0);
        assertThat(revision.id()).isEqualTo(revisionA2);
        assertThat(revision.versionArtefacto()).isEqualTo(versionA2);
        assertThat(revision.artefacto()).isEqualTo(artefactoA);
        assertThat(revision.version()).isEqualTo(2);
        assertThat(revision.estadoRevisionAsesor()).isEqualTo("EN_PROGRESO");
        assertThat(revision.estadoRevisionAsesorNombre()).isEqualTo("En progreso");
        assertThat(revision.fechaCreacion()).isEqualTo(FECHA_CREACION);
    }
}
