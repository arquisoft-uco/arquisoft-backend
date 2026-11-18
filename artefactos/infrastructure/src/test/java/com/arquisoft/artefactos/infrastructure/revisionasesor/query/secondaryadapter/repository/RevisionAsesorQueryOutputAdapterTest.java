package com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorEstudianteCriteria;
import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.mapper.ConsultarRevisionesAsesorEstudianteMapper;
import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.model.ConsultarRevisionesAsesorEstudianteQuery;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.observacion.support.ObservacionTestEntity;
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
class RevisionAsesorQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RevisionAsesorEstudianteQueryRepository repository;

    @Autowired
    private RevisionAsesorAsesorQueryRepository asesorRepository;

    private RevisionAsesorQueryOutputAdapter adapter;

    private final UUID estudianteA = UUID.randomUUID();
    private final UUID estudianteB = UUID.randomUUID();
    private final UUID estudianteC = UUID.randomUUID();

    private UUID versionA1;
    private UUID versionA2;
    private UUID versionB;
    private UUID versionC;
    private UUID revisionA1;
    private UUID revisionA2;
    private UUID revisionB;
    private UUID revisionC;
    private UUID artefactoCompartido;
    private UUID artefactoB;

    @BeforeEach
    void setUp() {
        adapter = new RevisionAsesorQueryOutputAdapter(repository, new RevisionAsesorEstudianteJpaSpecification(),
                asesorRepository, new RevisionAsesorAsesorJpaSpecification());
    }

    private void sembrarCatalogo() {
        entityManager.persist(new EstadoRevisionAsesorTestEntity("PENDIENTE", "Pendiente"));
        entityManager.persist(new EstadoRevisionAsesorTestEntity("EN_PROGRESO", "En progreso"));
    }

    private UUID sembrarArtefacto(String proyecto, String documento, int versionDocumento, UUID... estudiantes) {
        var proyectoId = UUID.randomUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(proyectoId, proyecto, null, "EN_PROCESO", null));
        var documentoId = UUID.randomUUID();
        entityManager.persist(new VersionRepositorioArtefactoJpaEntity(
                documentoId, documento, versionDocumento, UUID.randomUUID(), "Repo",
                "archivo.pdf", "application/pdf", 10L));
        var artefacto = UUID.randomUUID();
        entityManager.persist(new ArtefactoJpaEntity(artefacto, proyectoId, documentoId, UUID.randomUUID(), null));
        for (var estudiante : estudiantes) {
            entityManager.persist(new EstudianteArtefactoTestEntity(UUID.randomUUID(), artefacto, estudiante));
        }
        return artefacto;
    }

    private UUID sembrarVersion(UUID artefacto, int numero) {
        var version = UUID.randomUUID();
        entityManager.persist(new VersionArtefactoTestEntity(version, artefacto, numero));
        return version;
    }

    private UUID sembrarRevision(UUID version, String estado, String fecha) {
        var revision = UUID.randomUUID();
        entityManager.persist(new RevisionAsesorTestEntity(revision, version, estado, Instant.parse(fecha)));
        return revision;
    }

    private void sembrarObservacion(UUID revision, String descripcion, String estado) {
        entityManager.persist(new ObservacionTestEntity(UUID.randomUUID(), revision, descripcion, estado));
    }

    // Artefacto compartido (A y B), artefacto solo de B y artefacto solo de C.
    private void sembrarEscenario() {
        sembrarCatalogo();
        artefactoCompartido = sembrarArtefacto("Alfa", "Anteproyecto", 1, estudianteA, estudianteB);
        artefactoB = sembrarArtefacto("Beta", "Informe", 2, estudianteB);
        var artefactoC = sembrarArtefacto("Alfa", "Anteproyecto", 1, estudianteC);

        versionA1 = sembrarVersion(artefactoCompartido, 1);
        versionA2 = sembrarVersion(artefactoCompartido, 2);
        versionB = sembrarVersion(artefactoB, 1);
        versionC = sembrarVersion(artefactoC, 1);

        revisionA1 = sembrarRevision(versionA1, "PENDIENTE", "2026-10-01T10:00:00Z");
        revisionA2 = sembrarRevision(versionA2, "EN_PROGRESO", "2026-10-02T10:00:00Z");
        revisionB = sembrarRevision(versionB, "PENDIENTE", "2026-10-03T10:00:00Z");
        revisionC = sembrarRevision(versionC, "PENDIENTE", "2026-10-04T10:00:00Z");
        entityManager.flush();
        entityManager.clear();
    }

    private RevisionAsesorEstudianteCriteria criteria(
            UUID estudiante, NodoFiltro raiz, List<SortOrder> orden, int pagina, int tamanio) {
        var criterio = ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz);
        return ConsultarRevisionesAsesorEstudianteMapper.toCriteria(
                ConsultarRevisionesAsesorEstudianteQuery.crear(estudiante, criterio));
    }

    private RevisionAsesorEstudianteCriteria criteria(UUID estudiante, NodoFiltro raiz) {
        return criteria(estudiante, raiz, List.of(), 0, 10);
    }

    private static List<UUID> ids(List<RevisionAsesorReadModel> contenido) {
        return contenido.stream().map(RevisionAsesorReadModel::id).toList();
    }

    @Test
    void debeRetornarVacio_cuandoNoHayRevisionesEnBD() {
        // Arrange
        var criteria = criteria(estudianteA, null);

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeRetornarSoloRevisionesDeArtefactosDondeParticipaElEstudiante_cuandoHayOtrosEstudiantesYArtefactos() {
        // Arrange
        sembrarEscenario();

        // Act
        var deA = adapter.consultarTodasEstudiante(criteria(estudianteA, null));
        var deC = adapter.consultarTodasEstudiante(criteria(estudianteC, null));
        var deSinVinculo = adapter.consultarTodasEstudiante(criteria(UUID.randomUUID(), null));

        // Assert
        assertThat(ids(deA.getContent())).containsExactlyInAnyOrder(revisionA1, revisionA2);
        assertThat(ids(deC.getContent())).containsExactly(revisionC);
        assertThat(deSinVinculo.getContent()).isEmpty();
    }

    @Test
    void debeRetornarCadaRevisionUnaSolaVezPorEstudiante_cuandoElArtefactoTieneVariosEstudiantes() {
        // Arrange
        sembrarEscenario();

        // Act
        var deA = adapter.consultarTodasEstudiante(criteria(estudianteA, null));
        var deB = adapter.consultarTodasEstudiante(criteria(estudianteB, null));

        // Assert
        assertThat(deA.getTotalElements()).isEqualTo(2L);
        assertThat(ids(deA.getContent())).containsExactlyInAnyOrder(revisionA1, revisionA2);
        assertThat(deB.getTotalElements()).isEqualTo(3L);
        assertThat(ids(deB.getContent())).containsExactlyInAnyOrder(revisionA1, revisionA2, revisionB);
    }

    @Test
    void debeNoPermitirSustituirAlEstudianteDelToken_cuandoElBodyFiltraPorOtroEstudianteId() {
        // Arrange
        sembrarEscenario();
        var raizCliente = NodoFiltro.predicado("estudianteId", FiltroOperador.ES, estudianteC.toString());

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria(estudianteA, raizCliente));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeFiltrarPorVersionArtefacto_cuandoElCriteriaTraeEsePredicado() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("versionArtefacto", FiltroOperador.ES, versionA2.toString());

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria(estudianteA, raiz));
        var deVersionAjena = adapter.consultarTodasEstudiante(criteria(estudianteA,
                NodoFiltro.predicado("versionArtefacto", FiltroOperador.ES, versionC.toString())));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactly(revisionA2);
        assertThat(deVersionAjena.getContent()).isEmpty();
    }

    @Test
    void debeFiltrarPorEstadoRevisionAsesor_yRetornarVacioSiElEstadoNoExiste() {
        // Arrange
        sembrarEscenario();

        // Act
        var enProgreso = adapter.consultarTodasEstudiante(criteria(estudianteA,
                NodoFiltro.predicado("estadoRevisionAsesor", FiltroOperador.ES, "EN_PROGRESO")));
        var inexistente = adapter.consultarTodasEstudiante(criteria(estudianteA,
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
        var versionConEstado = NodoFiltro.grupo(FiltroConector.AND, List.of(
                NodoFiltro.predicado("versionArtefacto", FiltroOperador.ES, versionA1.toString()),
                NodoFiltro.predicado("estadoRevisionAsesor", FiltroOperador.ES, "PENDIENTE")));
        var versionConOtroEstado = NodoFiltro.grupo(FiltroConector.AND, List.of(
                NodoFiltro.predicado("versionArtefacto", FiltroOperador.ES, versionA1.toString()),
                NodoFiltro.predicado("estadoRevisionAsesor", FiltroOperador.ES, "EN_PROGRESO")));

        // Act
        var coincide = adapter.consultarTodasEstudiante(criteria(estudianteA, versionConEstado));
        var noCoincide = adapter.consultarTodasEstudiante(criteria(estudianteA, versionConOtroEstado));

        // Assert
        assertThat(ids(coincide.getContent())).containsExactly(revisionA1);
        assertThat(noCoincide.getContent()).isEmpty();
    }

    @Test
    void debeOrdenarPorNombreDelEstado_cuandoSePideAscendenteYDescendente() {
        // Arrange
        sembrarEscenario();
        var asc = List.of(SortOrder.of("estadoRevisionAsesor", SortDirection.ASC));
        var desc = List.of(SortOrder.of("estadoRevisionAsesor", SortDirection.DESC));

        // Act
        var ascendente = adapter.consultarTodasEstudiante(criteria(estudianteB, null, asc, 0, 10));
        var descendente = adapter.consultarTodasEstudiante(criteria(estudianteB, null, desc, 0, 10));

        // Assert
        assertThat(ascendente.getContent()).extracting(RevisionAsesorReadModel::estadoRevisionAsesor)
                .containsExactly("EN_PROGRESO", "PENDIENTE", "PENDIENTE");
        assertThat(descendente.getContent()).extracting(RevisionAsesorReadModel::estadoRevisionAsesor)
                .containsExactly("PENDIENTE", "PENDIENTE", "EN_PROGRESO");
    }

    @Test
    void debePaginar_cuandoHayMasRevisionesQueElTamanioDePagina() {
        // Arrange
        sembrarEscenario();
        var asc = List.of(SortOrder.of("estadoRevisionAsesor", SortDirection.ASC));

        // Act
        var primera = adapter.consultarTodasEstudiante(criteria(estudianteB, null, asc, 0, 2));
        var segunda = adapter.consultarTodasEstudiante(criteria(estudianteB, null, asc, 1, 2));

        // Assert
        assertThat(primera.getContent()).hasSize(2);
        assertThat(primera.getTotalElements()).isEqualTo(3L);
        assertThat(segunda.getContent()).hasSize(1);
        assertThat(segunda.getTotalElements()).isEqualTo(3L);
        assertThat(ids(primera.getContent())).doesNotContainAnyElementsOf(ids(segunda.getContent()));
    }

    @Test
    void debeProyectarArtefactoVersionEIdYNombreDelEstado_cuandoLaRevisionExiste() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("versionArtefacto", FiltroOperador.ES, versionA2.toString());

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria(estudianteA, raiz));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        var revision = resultado.getContent().get(0);
        assertThat(revision.id()).isEqualTo(revisionA2);
        assertThat(revision.versionArtefacto()).isEqualTo(versionA2);
        assertThat(revision.artefacto()).isEqualTo(artefactoCompartido);
        assertThat(revision.version()).isEqualTo(2);
        assertThat(revision.estadoRevisionAsesor()).isEqualTo("EN_PROGRESO");
        assertThat(revision.estadoRevisionAsesorNombre()).isEqualTo("En progreso");
    }

    @Test
    void debeProyectarFechaProyectoDocumentoYVersionDocumento_cuandoLaRevisionExiste() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("versionArtefacto", FiltroOperador.ES, versionB.toString());

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria(estudianteB, raiz));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        var revision = resultado.getContent().get(0);
        assertThat(revision.fechaCreacion()).isEqualTo(Instant.parse("2026-10-03T10:00:00Z"));
        assertThat(revision.nombreProyecto()).isEqualTo("Beta");
        assertThat(revision.nombreDocumento()).isEqualTo("Informe");
        assertThat(revision.versionDocumento()).isEqualTo(2);
    }

    @Test
    void debeContarObservacionesAbiertasYTotal_cuandoLaRevisionTieneObservacionesEnDistintosEstados() {
        // Arrange
        sembrarCatalogo();
        var artefacto = sembrarArtefacto("Alfa", "Anteproyecto", 1, estudianteA);
        var version = sembrarVersion(artefacto, 1);
        var revision = sembrarRevision(version, "PENDIENTE", "2026-10-01T10:00:00Z");
        sembrarObservacion(revision, "uno", "NUEVA");
        sembrarObservacion(revision, "dos", "EN_PROGRESO");
        sembrarObservacion(revision, "tres", "REABIERTA");
        sembrarObservacion(revision, "cuatro", "SOLUCIONADA");
        sembrarObservacion(revision, "cinco", "CERRADA");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria(estudianteA, null));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).observacionesAbiertas()).isEqualTo(3);
        assertThat(resultado.getContent().get(0).totalObservaciones()).isEqualTo(5);
    }

    @Test
    void debeRetornarCeroObservaciones_cuandoLaRevisionNoTiene() {
        // Arrange
        sembrarEscenario();

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria(estudianteA, null));

        // Assert
        assertThat(resultado.getContent()).hasSize(2)
                .allSatisfy(revision -> {
                    assertThat(revision.observacionesAbiertas()).isZero();
                    assertThat(revision.totalObservaciones()).isZero();
                });
    }

    @Test
    void debeFiltrarPorArtefactoYPorNombreDocumento_cuandoElCriteriaTraeEsosPredicados() {
        // Arrange
        sembrarEscenario();
        var porArtefacto = NodoFiltro.predicado("artefacto", FiltroOperador.ES, artefactoB.toString());
        var porDocumento = NodoFiltro.predicado("nombreDocumento", FiltroOperador.CONTIENE, "INFORM");

        // Act
        var conArtefacto = adapter.consultarTodasEstudiante(criteria(estudianteB, porArtefacto));
        var conDocumento = adapter.consultarTodasEstudiante(criteria(estudianteB, porDocumento));
        var artefactoAjeno = adapter.consultarTodasEstudiante(criteria(estudianteA, porArtefacto));

        // Assert
        assertThat(ids(conArtefacto.getContent())).containsExactly(revisionB);
        assertThat(ids(conDocumento.getContent())).containsExactly(revisionB);
        assertThat(artefactoAjeno.getContent()).isEmpty();
    }

    @Test
    void debeFiltrarPorRangoDeFechaCreacion_cuandoUsaMayorIgualYMenorIgual() {
        // Arrange
        sembrarEscenario();
        var rango = NodoFiltro.grupo(FiltroConector.AND, List.of(
                NodoFiltro.predicado("fechaCreacion", FiltroOperador.MAYOR_IGUAL_QUE, "2026-10-02T00:00:00"),
                NodoFiltro.predicado("fechaCreacion", FiltroOperador.MENOR_IGUAL_QUE, "2026-10-02T23:59:59")));

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria(estudianteB, rango));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactly(revisionA2);
    }

    @Test
    void debeOrdenarPorVersionYPorFechaCreacion_cuandoSePideDescendente() {
        // Arrange
        sembrarEscenario();
        var porVersion = List.of(SortOrder.of("version", SortDirection.DESC));
        var porFecha = List.of(SortOrder.of("fechaCreacion", SortDirection.DESC));

        // Act
        var conVersion = adapter.consultarTodasEstudiante(criteria(estudianteB, null, porVersion, 0, 10));
        var conFecha = adapter.consultarTodasEstudiante(criteria(estudianteB, null, porFecha, 0, 10));

        // Assert
        assertThat(conVersion.getContent()).extracting(RevisionAsesorReadModel::version)
                .containsExactly(2, 1, 1);
        assertThat(ids(conFecha.getContent())).containsExactly(revisionB, revisionA2, revisionA1);
    }

    @Test
    void debePaginarSinRepetirFilas_cuandoOrdenaPorVersionConEmpates() {
        // Arrange
        sembrarEscenario();
        var porVersion = List.of(SortOrder.of("version", SortDirection.ASC));

        // Act
        var primera = adapter.consultarTodasEstudiante(criteria(estudianteB, null, porVersion, 0, 1));
        var segunda = adapter.consultarTodasEstudiante(criteria(estudianteB, null, porVersion, 1, 1));
        var tercera = adapter.consultarTodasEstudiante(criteria(estudianteB, null, porVersion, 2, 1));

        // Assert
        var recorridas = new ArrayList<UUID>();
        recorridas.addAll(ids(primera.getContent()));
        recorridas.addAll(ids(segunda.getContent()));
        recorridas.addAll(ids(tercera.getContent()));
        assertThat(recorridas).containsExactlyInAnyOrder(revisionA1, revisionA2, revisionB).doesNotHaveDuplicates();
    }
}
