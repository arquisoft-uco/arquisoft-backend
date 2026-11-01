package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoSinAvanceCriteria;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.mapper.ConsultarArtefactosSinAvanceMapper;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosSinAvanceQuery;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoSinAvanceReadModel;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstadoRevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.RevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.VersionArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ArtefactoSinAvanceQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ArtefactoAsesorQueryRepository asesorRepository;

    @Autowired
    private ArtefactoEstudianteQueryRepository estudianteRepository;

    @Autowired
    private ArtefactoSinAvanceQueryRepository sinAvanceRepository;

    private ArtefactoQueryOutputAdapter adapter;

    private final UUID asesorA = UtilUUID.generarNuevoUUID();
    private final UUID asesorB = UtilUUID.generarNuevoUUID();

    private UUID proyectoAlfa;
    private UUID versionAlfaDoc1;
    private UUID versionAlfaDoc2;
    private UUID versionBetaDoc1;
    private UUID artefactoAlfa1;
    private UUID artefactoAlfa2;
    private UUID artefactoBeta1;
    private UUID artefactoDeB;

    @BeforeEach
    void setUp() {
        adapter = new ArtefactoQueryOutputAdapter(asesorRepository, new ArtefactoAsesorJpaSpecification(),
                estudianteRepository, new ArtefactoEstudianteJpaSpecification(),
                sinAvanceRepository, new ArtefactoSinAvanceJpaSpecification());
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

    private UUID sembrarArtefacto(UUID proyecto, UUID version, UUID asesor) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoJpaEntity(id, proyecto, version, asesor, null));
        return id;
    }

    private UUID sembrarVersionArtefacto(UUID artefacto, int numero, long diasAtras) {
        var id = UtilUUID.generarNuevoUUID();
        var fecha = Instant.now().minus(diasAtras, ChronoUnit.DAYS);
        entityManager.persist(new VersionArtefactoTestEntity(id, artefacto, numero, fecha, "artefactos/" + id));
        return id;
    }

    private void sembrarEscenario() {
        proyectoAlfa = sembrarProyecto("Alfa");
        var proyectoBeta = sembrarProyecto("Beta");
        versionAlfaDoc1 = sembrarVersionRepositorio("Anteproyecto", 1, "Repo Uno");
        versionAlfaDoc2 = sembrarVersionRepositorio("Informe", 2, "Repo Dos");
        versionBetaDoc1 = sembrarVersionRepositorio("Anteproyecto", 3, "Repo Uno");

        artefactoAlfa1 = sembrarArtefacto(proyectoAlfa, versionAlfaDoc2, asesorA);
        artefactoAlfa2 = sembrarArtefacto(proyectoAlfa, versionAlfaDoc1, asesorA);
        artefactoBeta1 = sembrarArtefacto(proyectoBeta, versionBetaDoc1, asesorA);
        artefactoDeB = sembrarArtefacto(proyectoAlfa, versionAlfaDoc1, asesorB);
        entityManager.flush();
        entityManager.clear();
    }

    private ArtefactoSinAvanceCriteria criteria(UUID asesor, NodoFiltro raiz, List<SortOrder> orden) {
        var criterio = ConsultaCriteriaQuery.crear(0, 10, orden, raiz);
        return ConsultarArtefactosSinAvanceMapper.toCriteria(
                ConsultarArtefactosSinAvanceQuery.crear(asesor, 30, criterio));
    }

    private ArtefactoSinAvanceCriteria criteria(UUID asesor) {
        return criteria(asesor, null, List.of());
    }

    private static List<UUID> ids(List<ArtefactoSinAvanceReadModel> contenido) {
        return contenido.stream().map(ArtefactoSinAvanceReadModel::id).toList();
    }

    @Test
    void debeIncluirArtefactoYTomarLaUltimaVersion_cuandoLaUltimaVersionEsAnteriorAlLimite() {
        // Arrange
        sembrarEscenario();
        sembrarVersionArtefacto(artefactoAlfa1, 1, 90);
        sembrarVersionArtefacto(artefactoAlfa1, 2, 40);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarSinAvance(criteria(asesorA));

        // Assert
        var alfa1 = resultado.getContent().stream()
                .filter(a -> a.id().equals(artefactoAlfa1)).findFirst().orElseThrow();
        assertThat(alfa1.diasSinAvance()).isBetween(39L, 40L);
        assertThat(alfa1.fechaUltimaVersion()).isAfter(Instant.now().minus(41, ChronoUnit.DAYS));
        assertThat(alfa1.nombreProyecto()).isEqualTo("Alfa");
        assertThat(alfa1.nombreDocumento()).isEqualTo("Informe");
        assertThat(alfa1.versionDocumento()).isEqualTo(2);
        assertThat(alfa1.nombreRepositorio()).isEqualTo("Repo Dos");
        assertThat(alfa1.proyectoGrado()).isEqualTo(proyectoAlfa);
        assertThat(alfa1.versionRepositorioArtefacto()).isEqualTo(versionAlfaDoc2);
    }

    @Test
    void debeIncluirArtefactoConCamposAusentes_cuandoNoTieneVersiones() {
        // Arrange
        sembrarEscenario();

        // Act
        var resultado = adapter.consultarSinAvance(criteria(asesorA));

        // Assert
        assertThat(ids(resultado.getContent()))
                .containsExactlyInAnyOrder(artefactoAlfa1, artefactoAlfa2, artefactoBeta1);
        assertThat(resultado.getContent()).allSatisfy(a -> {
            assertThat(a.fechaUltimaVersion()).isNull();
            assertThat(a.diasSinAvance()).isNull();
            assertThat(a.estadoUltimaRevision()).isNull();
        });
    }

    @Test
    void debeExcluirArtefacto_cuandoUltimaVersionEsRecienteAunqueExistaUnaVersionAntigua() {
        // Arrange
        sembrarEscenario();
        sembrarVersionArtefacto(artefactoAlfa1, 1, 90);
        sembrarVersionArtefacto(artefactoAlfa1, 2, 5);
        sembrarVersionArtefacto(artefactoAlfa2, 1, 60);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarSinAvance(criteria(asesorA));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactlyInAnyOrder(artefactoAlfa2, artefactoBeta1);
        assertThat(resultado.getTotalElements()).isEqualTo(2L);
    }

    @Test
    void debeExcluirArtefactosOmitidosYDeOtroAsesor_cuandoNoTienenVersiones() {
        // Arrange
        sembrarEscenario();
        var omitido = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoJpaEntity(omitido, proyectoAlfa, versionAlfaDoc1, asesorA, Instant.now()));
        entityManager.flush();
        entityManager.clear();

        // Act
        var deA = adapter.consultarSinAvance(criteria(asesorA));
        var deB = adapter.consultarSinAvance(criteria(asesorB));

        // Assert
        assertThat(ids(deA.getContent()))
                .containsExactlyInAnyOrder(artefactoAlfa1, artefactoAlfa2, artefactoBeta1)
                .doesNotContain(omitido, artefactoDeB);
        assertThat(ids(deB.getContent())).containsExactly(artefactoDeB);
    }

    @Test
    void debeRetornarEstadoDeLaUltimaRevision_cuandoHayVariasRevisiones() {
        // Arrange
        sembrarEscenario();
        entityManager.persist(new EstadoRevisionAsesorTestEntity("PENDIENTE", "Pendiente"));
        entityManager.persist(new EstadoRevisionAsesorTestEntity("CERRADA", "Cerrada"));
        var versionVieja = sembrarVersionArtefacto(artefactoAlfa1, 1, 90);
        var versionNueva = sembrarVersionArtefacto(artefactoAlfa1, 2, 80);
        entityManager.persist(new RevisionAsesorTestEntity(UtilUUID.generarNuevoUUID(), versionVieja, "CERRADA",
                Instant.now().minus(85, ChronoUnit.DAYS)));
        entityManager.persist(new RevisionAsesorTestEntity(UtilUUID.generarNuevoUUID(), versionNueva, "PENDIENTE",
                Instant.now().minus(70, ChronoUnit.DAYS)));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarSinAvance(criteria(asesorA));

        // Assert
        var alfa1 = resultado.getContent().stream()
                .filter(a -> a.id().equals(artefactoAlfa1)).findFirst().orElseThrow();
        assertThat(alfa1.estadoUltimaRevision()).isEqualTo("Pendiente");
    }

    @Test
    void debeFiltrarYOrdenar_cuandoElClienteEnviaFiltroYOrdenPorFechaUltimaVersion() {
        // Arrange
        sembrarEscenario();
        sembrarVersionArtefacto(artefactoAlfa1, 1, 50);
        sembrarVersionArtefacto(artefactoAlfa2, 1, 90);
        sembrarVersionArtefacto(artefactoBeta1, 1, 70);
        entityManager.flush();
        entityManager.clear();
        var orden = List.of(SortOrder.of("fechaUltimaVersion", SortDirection.ASC));
        var raiz = NodoFiltro.predicado("nombreProyecto", FiltroOperador.CONTIENE, "alf");

        // Act
        var ordenado = adapter.consultarSinAvance(criteria(asesorA, null, orden));
        var filtrado = adapter.consultarSinAvance(criteria(asesorA, raiz, List.of()));

        // Assert
        assertThat(ids(ordenado.getContent()))
                .containsExactly(artefactoAlfa2, artefactoBeta1, artefactoAlfa1);
        assertThat(ids(filtrado.getContent())).containsExactlyInAnyOrder(artefactoAlfa1, artefactoAlfa2);
    }
}
