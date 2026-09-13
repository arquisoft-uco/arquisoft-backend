package com.arquisoft.fichas.infrastructure.revisionitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemCriteria;
import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.entity.EstadoRevisionJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.entity.FichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.entity.ItemFichaPerfilJpaEntity;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RevisionItemQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RevisionItemQueryRepository revisionItemRepository;

    private RevisionItemQueryOutputAdapter adapter;

    private EstadoRevisionJpaEntity estadoEnProgreso;
    private EstadoRevisionJpaEntity estadoCerrada;
    private TipoItemJpaEntity tipoItem;

    @BeforeEach
    void setUp() {
        adapter = new RevisionItemQueryOutputAdapter(revisionItemRepository, new RevisionItemJpaSpecification());

        estadoEnProgreso = entityManager.persist(EstadoRevisionJpaEntity.builder()
                .id("EN_PROGRESO").nombre("En Progreso").descripcion("La revision esta en progreso")
                .build());
        estadoCerrada = entityManager.persist(EstadoRevisionJpaEntity.builder()
                .id("CERRADA").nombre("Cerrada").descripcion("La revision fue cerrada")
                .build());
        tipoItem = entityManager.persist(TipoItemJpaEntity.builder()
                .id("OBJETIVO_GENERAL").nombre("Objetivo General").descripcion("Objetivo general del proyecto")
                .build());
    }

    @Test
    void debeRetornarVacio_cuandoNoHayRevisionesEnBD() {
        // Arrange
        var criteria = RevisionItemCriteria.builder().pagina(0).tamanio(10).build();

        // Act
        PaginatedResult<RevisionItemReadModel> resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeFiltrarPorItem_cuandoElCriteriaTraeEsePredicado() {
        // Arrange
        var asesor = persistirAsesor("Ana Ramirez", "ana@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto de Ana");
        var itemBuscado = persistirItem(ficha, "Contenido del item buscado");
        var otroItem = persistirItem(ficha, "Contenido de otro item");
        persistirRevision(itemBuscado, estadoEnProgreso);
        persistirRevision(otroItem, estadoEnProgreso);
        entityManager.flush();

        var criteria = RevisionItemCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("item", FiltroOperador.ES, itemBuscado.toString()))
                .build();

        // Act
        PaginatedResult<RevisionItemReadModel> resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).item()).isEqualTo(itemBuscado);
    }

    @Test
    void debeFiltrarPorEstadoRevision_cuandoElCriteriaTraeEsePredicado() {
        // Arrange
        var asesor = persistirAsesor("Juan Salazar", "juan@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto de Juan");
        var itemUno = persistirItem(ficha, "Item uno");
        var itemDos = persistirItem(ficha, "Item dos");
        persistirRevision(itemUno, estadoEnProgreso);
        persistirRevision(itemDos, estadoCerrada);
        entityManager.flush();

        var criteria = RevisionItemCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estadoRevision", FiltroOperador.ES, "CERRADA"))
                .build();

        // Act
        PaginatedResult<RevisionItemReadModel> resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).estadoRevision()).isEqualTo("CERRADA");
        assertThat(resultado.getContent().get(0).estadoRevisionNombre()).isEqualTo("Cerrada");
    }

    @Test
    void debeFiltrarPorAsesorId_scopeDelAsesorQueConsulta() {
        // Arrange
        var asesorUno = persistirAsesor("Carla Diaz", "carla@soyuco.edu.co");
        var asesorDos = persistirAsesor("Luis Peña", "luis@soyuco.edu.co");
        var fichaAsesorUno = persistirFicha(asesorUno, "Proyecto uno");
        var fichaAsesorDos = persistirFicha(asesorDos, "Proyecto dos");
        var itemAsesorUno = persistirItem(fichaAsesorUno, "Item de asesor uno");
        var itemAsesorDos = persistirItem(fichaAsesorDos, "Item de asesor dos");
        persistirRevision(itemAsesorUno, estadoEnProgreso);
        persistirRevision(itemAsesorDos, estadoEnProgreso);
        entityManager.flush();

        var criteria = RevisionItemCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("asesorId", FiltroOperador.ES, asesorUno.toString()))
                .build();

        // Act
        PaginatedResult<RevisionItemReadModel> resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).item()).isEqualTo(itemAsesorUno);
    }

    @Test
    void debePaginar_cuandoSeSolicitaUnTamanioMenorQueElTotal() {
        // Arrange
        var asesor = persistirAsesor("Marco Vidal", "marco@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto de Marco");
        for (int i = 0; i < 3; i++) {
            persistirRevision(persistirItem(ficha, "Item " + i), estadoEnProgreso);
        }
        entityManager.flush();

        var criteria = RevisionItemCriteria.builder().pagina(0).tamanio(2).build();

        // Act
        PaginatedResult<RevisionItemReadModel> resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getTotalElements()).isEqualTo(3L);
        assertThat(resultado.getPage()).isZero();
        assertThat(resultado.getSize()).isEqualTo(2);
    }

    @Test
    void debeOrdenarPorEstadoRevision_cuandoElCriteriaLoPide() {
        // Arrange
        var asesor = persistirAsesor("Zulma Torres", "zulma@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto de Zulma");
        persistirRevision(persistirItem(ficha, "Item en progreso"), estadoEnProgreso);
        persistirRevision(persistirItem(ficha, "Item cerrado"), estadoCerrada);
        entityManager.flush();

        var criteria = RevisionItemCriteria.builder()
                .pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("estadoRevision", SortDirection.DESC)))
                .build();

        // Act
        PaginatedResult<RevisionItemReadModel> resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(RevisionItemReadModel::estadoRevisionNombre)
                .containsExactly("En Progreso", "Cerrada");
    }

    private UUID persistirAsesor(String nombre, String email) {
        var asesor = AsesorFichaJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("DOC-" + UUID.randomUUID().toString().substring(0, 8))
                .nombre(nombre)
                .email(email)
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

    private UUID persistirItem(UUID fichaPerfilId, String contenido) {
        var item = ItemFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .tipoItem(tipoItem)
                .contenido(contenido)
                .build();
        entityManager.persist(item);
        return item.getId();
    }

    private UUID persistirRevision(UUID itemId, EstadoRevisionJpaEntity estado) {
        var revision = RevisionItemJpaEntity.builder()
                .id(UUID.randomUUID())
                .itemId(itemId)
                .estadoRevision(estado)
                .fechaCreacion(Instant.now())
                .build();
        entityManager.persist(revision);
        return revision.getId();
    }
}
