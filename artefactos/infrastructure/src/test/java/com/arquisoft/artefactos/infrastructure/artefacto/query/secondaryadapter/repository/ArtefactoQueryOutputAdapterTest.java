package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoAsesorCriteria;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.mapper.ConsultarArtefactosAsesorMapper;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosAsesorQuery;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ArtefactoQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ArtefactoAsesorQueryRepository repository;

    private ArtefactoQueryOutputAdapter adapter;

    private final UUID asesorA = UtilUUID.generarNuevoUUID();
    private final UUID asesorB = UtilUUID.generarNuevoUUID();

    private UUID proyectoAlfa;
    private UUID proyectoBeta;
    private UUID versionAlfaDoc1;
    private UUID versionAlfaDoc2;
    private UUID versionBetaDoc1;
    private UUID artefactoAlfa1;
    private UUID artefactoAlfa2;
    private UUID artefactoBeta1;
    private UUID artefactoDeB;

    @BeforeEach
    void setUp() {
        adapter = new ArtefactoQueryOutputAdapter(repository, new ArtefactoAsesorJpaSpecification());
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

    private UUID sembrarArtefacto(UUID proyecto, UUID version, UUID asesor) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoJpaEntity(id, proyecto, version, asesor));
        return id;
    }

    private void sembrarEscenario() {
        proyectoAlfa = sembrarProyecto("Alfa");
        proyectoBeta = sembrarProyecto("Beta");
        versionAlfaDoc1 = sembrarVersion("Anteproyecto", 1, "Repo Uno");
        versionAlfaDoc2 = sembrarVersion("Informe", 2, "Repo Dos");
        versionBetaDoc1 = sembrarVersion("Anteproyecto", 3, "Repo Uno");

        artefactoAlfa1 = sembrarArtefacto(proyectoAlfa, versionAlfaDoc2, asesorA);
        artefactoAlfa2 = sembrarArtefacto(proyectoAlfa, versionAlfaDoc1, asesorA);
        artefactoBeta1 = sembrarArtefacto(proyectoBeta, versionBetaDoc1, asesorA);
        artefactoDeB = sembrarArtefacto(proyectoAlfa, versionAlfaDoc1, asesorB);
        entityManager.flush();
        entityManager.clear();
    }

    private ArtefactoAsesorCriteria criteria(
            UUID asesor, NodoFiltro raiz, List<SortOrder> orden, int pagina, int tamanio) {
        var criterio = ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz);
        return ConsultarArtefactosAsesorMapper.toCriteria(
                ConsultarArtefactosAsesorQuery.crear(asesor, criterio));
    }

    private ArtefactoAsesorCriteria criteria(UUID asesor, NodoFiltro raiz) {
        return criteria(asesor, raiz, List.of(), 0, 10);
    }

    private static List<UUID> ids(List<ArtefactoReadModel> contenido) {
        return contenido.stream().map(ArtefactoReadModel::id).toList();
    }

    @Test
    void debeDevolverSoloArtefactosDelAsesor_cuandoHayDeOtros() {
        // Arrange
        sembrarEscenario();

        // Act
        var deA = adapter.consultarTodosAsesor(criteria(asesorA, null));
        var deB = adapter.consultarTodosAsesor(criteria(asesorB, null));

        // Assert
        assertThat(deA.getTotalElements()).isEqualTo(3L);
        assertThat(ids(deA.getContent())).containsExactlyInAnyOrder(artefactoAlfa1, artefactoAlfa2, artefactoBeta1);
        assertThat(ids(deB.getContent())).containsExactly(artefactoDeB);
    }

    @Test
    void debeNoPermitirSustituirAlAsesorDelToken_cuandoElBodyFiltraPorOtroAsesorId() {
        // Arrange
        sembrarEscenario();
        var raizCliente = NodoFiltro.predicado("asesorId", FiltroOperador.ES, asesorB.toString());

        // Act
        var resultado = adapter.consultarTodosAsesor(criteria(asesorA, raizCliente));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
    }

    @Test
    void debeOrdenarPorNombreProyectoYDocumento_cuandoOrdenPorDefecto() {
        // Arrange
        sembrarEscenario();

        // Act
        var resultado = adapter.consultarTodosAsesor(criteria(asesorA, null));

        // Assert
        assertThat(ids(resultado.getContent()))
                .containsExactly(artefactoAlfa2, artefactoAlfa1, artefactoBeta1);
    }

    @Test
    void debeFiltrarPorNombreProyecto_cuandoContiene() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("nombreProyecto", FiltroOperador.CONTIENE, "bet");

        // Act
        var resultado = adapter.consultarTodosAsesor(criteria(asesorA, raiz));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactly(artefactoBeta1);
    }

    @Test
    void debeFiltrarPorVersionDocumento_cuandoEs() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("versionDocumento", FiltroOperador.ES, "2");

        // Act
        var resultado = adapter.consultarTodosAsesor(criteria(asesorA, raiz));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactly(artefactoAlfa1);
    }

    @Test
    void debeFiltrarPorNombreRepositorio_cuandoContiene() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("nombreRepositorio", FiltroOperador.CONTIENE, "dos");

        // Act
        var resultado = adapter.consultarTodosAsesor(criteria(asesorA, raiz));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactly(artefactoAlfa1);
    }

    @Test
    void debeFiltrarPorProyectoGradoYVersionRepositorio_cuandoEs() {
        // Arrange
        sembrarEscenario();
        var porProyecto = NodoFiltro.predicado("proyectoGrado", FiltroOperador.ES, proyectoBeta.toString());
        var porVersion = NodoFiltro.predicado("versionRepositorioArtefacto", FiltroOperador.ES,
                versionAlfaDoc1.toString());

        // Act
        var conProyecto = adapter.consultarTodosAsesor(criteria(asesorA, porProyecto));
        var conVersion = adapter.consultarTodosAsesor(criteria(asesorA, porVersion));

        // Assert
        assertThat(ids(conProyecto.getContent())).containsExactly(artefactoBeta1);
        assertThat(ids(conVersion.getContent())).containsExactly(artefactoAlfa2);
    }

    @Test
    void debeOrdenarPorVersionDocumento_cuandoElClienteLoPide() {
        // Arrange
        sembrarEscenario();
        var orden = List.of(SortOrder.of("versionDocumento", SortDirection.DESC));

        // Act
        var resultado = adapter.consultarTodosAsesor(criteria(asesorA, null, orden, 0, 10));

        // Assert
        assertThat(resultado.getContent()).extracting(ArtefactoReadModel::versionDocumento)
                .containsExactly(3, 2, 1);
    }

    @Test
    void debeDevolverPaginaVacia_cuandoNoHayCoincidencias() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("nombreProyecto", FiltroOperador.CONTIENE, "inexistente");

        // Act
        var resultado = adapter.consultarTodosAsesor(criteria(asesorA, raiz));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeMapearTodosLosCampos_cuandoHayResultado() {
        // Arrange
        sembrarEscenario();
        var raiz = NodoFiltro.predicado("versionDocumento", FiltroOperador.ES, "3");

        // Act
        var resultado = adapter.consultarTodosAsesor(criteria(asesorA, raiz));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        var artefacto = resultado.getContent().get(0);
        assertThat(artefacto.id()).isEqualTo(artefactoBeta1);
        assertThat(artefacto.proyectoGrado()).isEqualTo(proyectoBeta);
        assertThat(artefacto.nombreProyecto()).isEqualTo("Beta");
        assertThat(artefacto.versionRepositorioArtefacto()).isEqualTo(versionBetaDoc1);
        assertThat(artefacto.nombreDocumento()).isEqualTo("Anteproyecto");
        assertThat(artefacto.versionDocumento()).isEqualTo(3);
        assertThat(artefacto.nombreRepositorio()).isEqualTo("Repo Uno");
    }
}
