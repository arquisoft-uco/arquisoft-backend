package com.arquisoft.fichas.infrastructure.observacionitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemCriteria;
import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoobservacionrevision.command.secondaryadapter.entity.EstadoObservacionRevisionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.entity.EstadoRevisionJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.entity.FichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.entity.ItemFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.entity.ObservacionItemJpaEntity;
import com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.entity.RevisionItemJpaEntity;
import com.arquisoft.fichas.infrastructure.tipoitem.command.secondaryadapter.entity.TipoItemJpaEntity;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ObservacionItemQueryOutputAdapterTest {

    private static final int ORDEN_POR_DEFECTO_DEL_CASE = 4;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ObservacionItemQueryRepository observacionItemRepository;

    private ObservacionItemQueryOutputAdapter adapter;

    private EstadoObservacionRevisionJpaEntity pendiente;
    private EstadoObservacionRevisionJpaEntity enProgreso;
    private EstadoObservacionRevisionJpaEntity cerrado;
    private EstadoRevisionJpaEntity estadoRevision;
    private TipoItemJpaEntity tipoItem;

    @BeforeEach
    void setUp() {
        adapter = new ObservacionItemQueryOutputAdapter(
                observacionItemRepository, new ObservacionItemJpaSpecification());

        pendiente = entityManager.persist(EstadoObservacionRevisionJpaEntity.builder()
                .id("PENDIENTE").nombre("Pendiente").descripcion("La observacion esta pendiente").build());
        enProgreso = entityManager.persist(EstadoObservacionRevisionJpaEntity.builder()
                .id("EN_PROGRESO").nombre("En Progreso").descripcion("La observacion esta en progreso").build());
        cerrado = entityManager.persist(EstadoObservacionRevisionJpaEntity.builder()
                .id("CERRADO").nombre("Cerrado").descripcion("La observacion esta cerrada").build());
        estadoRevision = entityManager.persist(EstadoRevisionJpaEntity.builder()
                .id("EN_PROGRESO").nombre("En Progreso").descripcion("La revision esta en progreso").build());
        tipoItem = entityManager.persist(TipoItemJpaEntity.builder()
                .id("OBJETIVO_GENERAL").nombre("Objetivo General").descripcion("Objetivo general del proyecto")
                .build());
    }

    @Test
    void debeRetornarPaginaVacia_cuandoNoHayObservacionesEnBD() {
        // Arrange
        var criteria = ObservacionItemCriteria.builder().pagina(0).tamanio(10).build();

        // Act
        var resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeRetornarSoloObservacionesDelAsesorForzado_yExcluirLasDeOtroAsesor() {
        // Arrange
        var asesorUno = persistirAsesor("Carla Diaz", "carla@soyuco.edu.co");
        var asesorDos = persistirAsesor("Luis Peña", "luis@soyuco.edu.co");
        var revisionAsesorUno = persistirRevisionDeNuevoItem(persistirFicha(asesorUno, "Proyecto uno"));
        var revisionAsesorDos = persistirRevisionDeNuevoItem(persistirFicha(asesorDos, "Proyecto dos"));
        var propiaUno = persistirObservacion(revisionAsesorUno, "Observacion propia uno", pendiente);
        var propiaDos = persistirObservacion(revisionAsesorUno, "Observacion propia dos", cerrado);
        persistirObservacion(revisionAsesorDos, "Observacion de otro asesor", pendiente);
        entityManager.flush();

        var criteria = ObservacionItemCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("asesorId", FiltroOperador.ES, asesorUno.toString()))
                .build();

        // Act
        var resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(2L);
        assertThat(resultado.getContent())
                .extracting(ObservacionItemReadModel::id)
                .containsExactlyInAnyOrder(propiaUno, propiaDos);
    }

    @Test
    void debeFiltrarPorRevisionItem_cuandoElCriteriaTraeEsePredicado() {
        // Arrange
        var asesor = persistirAsesor("Ana Ramirez", "ana@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto de Ana");
        var revisionBuscada = persistirRevisionDeNuevoItem(ficha);
        var otraRevision = persistirRevisionDeNuevoItem(ficha);
        var buscada = persistirObservacion(revisionBuscada, "Observacion de la revision buscada", pendiente);
        persistirObservacion(otraRevision, "Observacion de otra revision", pendiente);
        entityManager.flush();

        var criteria = ObservacionItemCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("revisionItem", FiltroOperador.ES, revisionBuscada.toString()))
                .build();

        // Act
        var resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent()).singleElement().satisfies(observacion -> {
            assertThat(observacion.id()).isEqualTo(buscada);
            assertThat(observacion.revisionItem()).isEqualTo(revisionBuscada);
            assertThat(observacion.observacion()).isEqualTo("Observacion de la revision buscada");
        });
    }

    @Test
    void debeFiltrarPorEstadoObservacionRevision_yResolverSuNombrePorElJoin() {
        // Arrange
        var asesor = persistirAsesor("Juan Salazar", "juan@soyuco.edu.co");
        var revision = persistirRevisionDeNuevoItem(persistirFicha(asesor, "Proyecto de Juan"));
        persistirObservacion(revision, "Observacion pendiente", pendiente);
        persistirObservacion(revision, "Observacion en progreso", enProgreso);
        persistirObservacion(revision, "Observacion cerrada", cerrado);
        entityManager.flush();

        var criteria = ObservacionItemCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estadoObservacionRevision", FiltroOperador.ES, "EN_PROGRESO"))
                .build();

        // Act
        var resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent()).singleElement().satisfies(observacion -> {
            assertThat(observacion.estadoObservacionRevision()).isEqualTo("EN_PROGRESO");
            assertThat(observacion.estadoObservacionRevisionNombre()).isEqualTo("En Progreso");
        });
    }

    @Test
    void debeOrdenarPorCicloDeVidaAscendente_cuandoElCriteriaPideEstadoAsc() {
        // Arrange
        sembrarUnaObservacionPorEstadoEnOrdenAlfabeticoInverso();
        var criteria = ObservacionItemCriteria.builder()
                .pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("estadoObservacionRevision", SortDirection.ASC)))
                .build();

        // Act
        var resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(ObservacionItemReadModel::estadoObservacionRevision)
                .containsExactly("PENDIENTE", "EN_PROGRESO", "CERRADO");
    }

    @Test
    void debeOrdenarPorCicloDeVidaDescendente_cuandoElCriteriaPideEstadoDesc() {
        // Arrange
        sembrarUnaObservacionPorEstadoEnOrdenAlfabeticoInverso();
        var criteria = ObservacionItemCriteria.builder()
                .pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("estadoObservacionRevision", SortDirection.DESC)))
                .build();

        // Act
        var resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(ObservacionItemReadModel::estadoObservacionRevision)
                .containsExactly("CERRADO", "EN_PROGRESO", "PENDIENTE");
    }

    @Test
    void debePaginarSinRepetirNiPerderFilas_yDesempatarPorId_cuandoHayEmpatesDeEstadoYTamanioDos() {
        // Arrange
        var asesor = persistirAsesor("Marco Vidal", "marco@soyuco.edu.co");
        var revision = persistirRevisionDeNuevoItem(persistirFicha(asesor, "Proyecto de Marco"));
        persistirObservacion(idFijo(4), revision, "Observacion 4", cerrado);
        persistirObservacion(idFijo(2), revision, "Observacion 2", pendiente);
        persistirObservacion(idFijo(6), revision, "Observacion 6", enProgreso);
        persistirObservacion(idFijo(3), revision, "Observacion 3", cerrado);
        persistirObservacion(idFijo(1), revision, "Observacion 1", pendiente);
        persistirObservacion(idFijo(5), revision, "Observacion 5", enProgreso);
        entityManager.flush();

        var recorridas = new ArrayList<ObservacionItemReadModel>();
        PaginatedResult<ObservacionItemReadModel> pagina;
        var numeroPagina = 0;

        // Act
        do {
            pagina = adapter.consultarTodas(ObservacionItemCriteria.builder()
                    .pagina(numeroPagina).tamanio(2)
                    .ordenamiento(List.of(SortOrder.of("estadoObservacionRevision", SortDirection.ASC)))
                    .build());
            recorridas.addAll(pagina.getContent());
            numeroPagina++;
        } while (!pagina.isLast());

        // Assert
        assertThat(pagina.getTotalElements()).isEqualTo(6L);
        assertThat(pagina.getTotalPages()).isEqualTo(3);
        assertThat(recorridas).extracting(ObservacionItemReadModel::id)
                .containsExactly(idFijo(1), idFijo(2), idFijo(5), idFijo(6), idFijo(3), idFijo(4));
    }

    @Test
    void debeRetornarTodasOrdenadasPorId_cuandoElCriteriaNoTraeOrdenamiento() {
        // Arrange
        var asesor = persistirAsesor("Nora Prieto", "nora@soyuco.edu.co");
        var revision = persistirRevisionDeNuevoItem(persistirFicha(asesor, "Proyecto de Nora"));
        persistirObservacion(idFijo(3), revision, "Observacion 3", cerrado);
        persistirObservacion(idFijo(1), revision, "Observacion 1", pendiente);
        persistirObservacion(idFijo(2), revision, "Observacion 2", enProgreso);
        entityManager.flush();
        var criteria = ObservacionItemCriteria.builder().pagina(0).tamanio(10).build();

        // Act
        var resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(3L);
        assertThat(resultado.getContent()).extracting(ObservacionItemReadModel::id)
                .containsExactly(idFijo(1), idFijo(2), idFijo(3));
    }

    @Test
    void debeAsignarUnOrdenPropioAcadaEstadoDelDominio_sinCaerEnLaRamaPorDefectoDelCase() {
        // Arrange
        var asesor = persistirAsesor("Olga Mejia", "olga@soyuco.edu.co");
        var revision = persistirRevisionDeNuevoItem(persistirFicha(asesor, "Proyecto de Olga"));
        for (var estadoDominio : EstadoObservacionRevision.values()) {
            var estado = Optional.ofNullable(
                            entityManager.find(EstadoObservacionRevisionJpaEntity.class, estadoDominio.getId()))
                    .orElseGet(() -> entityManager.persist(EstadoObservacionRevisionJpaEntity.builder()
                            .id(estadoDominio.getId()).nombre(estadoDominio.getNombre())
                            .descripcion("Estado del dominio").build()));
            persistirObservacion(revision, "Observacion " + estadoDominio.getId(), estado);
        }
        entityManager.flush();

        // Act
        var ordenes = observacionItemRepository.findAll().stream()
                .map(ObservacionItemJpaQueryEntity::getEstadoOrden)
                .toList();

        // Assert
        assertThat(ordenes)
                .hasSize(EstadoObservacionRevision.values().length)
                .doesNotHaveDuplicates()
                .doesNotContain(ORDEN_POR_DEFECTO_DEL_CASE);
    }

    private void sembrarUnaObservacionPorEstadoEnOrdenAlfabeticoInverso() {
        var asesor = persistirAsesor("Zulma Torres", "zulma@soyuco.edu.co");
        var revision = persistirRevisionDeNuevoItem(persistirFicha(asesor, "Proyecto de Zulma"));
        persistirObservacion(revision, "Observacion cerrada", cerrado);
        persistirObservacion(revision, "Observacion pendiente", pendiente);
        persistirObservacion(revision, "Observacion en progreso", enProgreso);
        entityManager.flush();
    }

    private UUID persistirAsesor(String nombre, String email) {
        var asesor = AsesorFichaJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("DOC-" + UUID.randomUUID().toString().substring(0, 8))
                .nombre(nombre)
                .email(email)
                .ocurridoEn(Instant.now())
                .build();
        entityManager.persist(asesor);
        return asesor.getId();
    }

    private UUID persistirFicha(UUID asesorId, String titulo) {
        var asesorRef = entityManager.find(AsesorFichaJpaEntity.class, asesorId);
        var ficha = FichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto(titulo)
                .asesorFicha(asesorRef)
                .build();
        entityManager.persist(ficha);
        return ficha.getId();
    }

    private UUID persistirRevisionDeNuevoItem(UUID fichaPerfilId) {
        var item = ItemFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .tipoItem(tipoItem)
                .contenido("Contenido " + UUID.randomUUID())
                .build();
        entityManager.persist(item);

        var revision = RevisionItemJpaEntity.builder()
                .id(UUID.randomUUID())
                .itemId(item.getId())
                .estadoRevision(estadoRevision)
                .fechaCreacion(Instant.now())
                .build();
        entityManager.persist(revision);
        return revision.getId();
    }

    private static UUID idFijo(long numero) {
        return new UUID(0L, numero);
    }

    private UUID persistirObservacion(UUID revisionItemId, String texto, EstadoObservacionRevisionJpaEntity estado) {
        return persistirObservacion(UUID.randomUUID(), revisionItemId, texto, estado);
    }

    private UUID persistirObservacion(
            UUID id, UUID revisionItemId, String texto, EstadoObservacionRevisionJpaEntity estado) {
        var observacion = ObservacionItemJpaEntity.builder()
                .id(id)
                .revisionItemId(revisionItemId)
                .observacion(texto)
                .estadoObservacionRevision(estado)
                .build();
        entityManager.persist(observacion);
        return observacion.getId();
    }
}
