package com.arquisoft.fichas.infrastructure.revisionitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemCriteria;
import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemEstudianteCriteria;
import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoobservacionrevision.command.secondaryadapter.entity.EstadoObservacionRevisionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.entity.EstadoRevisionJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RevisionItemQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RevisionItemQueryRepository revisionItemRepository;

    @Autowired
    private RevisionItemEstudianteQueryRepository revisionItemEstudianteRepository;

    private RevisionItemQueryOutputAdapter adapter;

    private EstadoRevisionJpaEntity estadoEnProgreso;
    private EstadoRevisionJpaEntity estadoCerrada;
    private TipoItemJpaEntity tipoItem;
    private EstadoObservacionRevisionJpaEntity estadoPendiente;

    @BeforeEach
    void setUp() {
        adapter = new RevisionItemQueryOutputAdapter(revisionItemRepository, new RevisionItemJpaSpecification(),
                revisionItemEstudianteRepository, new RevisionItemEstudianteJpaSpecification());

        estadoEnProgreso = entityManager.persist(EstadoRevisionJpaEntity.builder()
                .id("EN_PROGRESO").nombre("En Progreso").descripcion("La revision esta en progreso")
                .build());
        estadoCerrada = entityManager.persist(EstadoRevisionJpaEntity.builder()
                .id("CERRADA").nombre("Cerrada").descripcion("La revision fue cerrada")
                .build());
        tipoItem = entityManager.persist(TipoItemJpaEntity.builder()
                .id("OBJETIVO_GENERAL").nombre("Objetivo General").descripcion("Objetivo general del proyecto")
                .build());
        estadoPendiente = entityManager.persist(EstadoObservacionRevisionJpaEntity.builder()
                .id("PENDIENTE").nombre("Pendiente").descripcion("La observacion esta pendiente")
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

    @Test
    void debeRetornarRevision_cuandoTieneObservacionYEstudianteVinculado() {
        // Arrange
        var asesor = persistirAsesor("Rosa Nieto", "rosa@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto de Rosa");
        var item = persistirItem(ficha, "Item con observacion");
        var revision = persistirRevision(item, estadoEnProgreso);
        var estudiante = persistirEstudiante("Pedro Lara", "pedro@soyuco.edu.co");
        persistirVinculoEstudianteFicha(ficha, estudiante);
        persistirObservacion(revision);
        entityManager.flush();

        var criteria = RevisionItemEstudianteCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estudianteId", FiltroOperador.ES, estudiante.toString()))
                .build();

        // Act
        PaginatedResult<RevisionItemReadModel> resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).item()).isEqualTo(item);
    }

    @Test
    void debeExcluirRevision_cuandoNoTieneObservaciones() {
        // Arrange — POL-12: sin observacion asociada, la revision no debe aparecer aunque el
        // estudiante esté correctamente vinculado a la ficha.
        var asesor = persistirAsesor("Hugo Campos", "hugo@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto de Hugo");
        var item = persistirItem(ficha, "Item sin observacion");
        persistirRevision(item, estadoEnProgreso);
        var estudiante = persistirEstudiante("Nadia Rios", "nadia@soyuco.edu.co");
        persistirVinculoEstudianteFicha(ficha, estudiante);
        entityManager.flush();

        var criteria = RevisionItemEstudianteCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estudianteId", FiltroOperador.ES, estudiante.toString()))
                .build();

        // Act
        PaginatedResult<RevisionItemReadModel> resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeExcluirRevision_cuandoEstudianteNoEstaVinculadoALaFicha() {
        // Arrange — la revision tiene observacion (pasaria POL-12), pero el estudiante consultado
        // no aparece en estudiante_ficha_perfil para esa ficha.
        var asesor = persistirAsesor("Elena Puentes", "elena@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto de Elena");
        var item = persistirItem(ficha, "Item con observacion");
        var revision = persistirRevision(item, estadoEnProgreso);
        persistirObservacion(revision);
        var estudianteVinculado = persistirEstudiante("Andres Soto", "andres@soyuco.edu.co");
        persistirVinculoEstudianteFicha(ficha, estudianteVinculado);
        var estudianteAjeno = persistirEstudiante("Camila Ruiz", "camila@soyuco.edu.co");
        entityManager.flush();

        var criteria = RevisionItemEstudianteCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estudianteId", FiltroOperador.ES, estudianteAjeno.toString()))
                .build();

        // Act
        PaginatedResult<RevisionItemReadModel> resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
    }

    @Test
    void debeExcluirDuplicados_cuandoFichaTieneVariosEstudiantes() {
        // Arrange — el JOIN con estudiante_ficha_perfil no debe multiplicar la fila de la revision
        // para el estudiante consultado, aunque la ficha tenga mas de un estudiante vinculado.
        var asesor = persistirAsesor("Fabian Leal", "fabian@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto compartido");
        var item = persistirItem(ficha, "Item compartido");
        var revision = persistirRevision(item, estadoEnProgreso);
        persistirObservacion(revision);
        var estudianteUno = persistirEstudiante("Estudiante Uno", "uno@soyuco.edu.co");
        var estudianteDos = persistirEstudiante("Estudiante Dos", "dos@soyuco.edu.co");
        persistirVinculoEstudianteFicha(ficha, estudianteUno);
        persistirVinculoEstudianteFicha(ficha, estudianteDos);
        entityManager.flush();

        var criteria = RevisionItemEstudianteCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estudianteId", FiltroOperador.ES, estudianteUno.toString()))
                .build();

        // Act
        PaginatedResult<RevisionItemReadModel> resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void debeFiltrarPorItem_cuandoCriteriaLoIndica() {
        // Arrange
        var asesor = persistirAsesor("Gloria Mesa", "gloria@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto de Gloria");
        var itemBuscado = persistirItem(ficha, "Item buscado");
        var otroItem = persistirItem(ficha, "Otro item");
        var revisionBuscada = persistirRevision(itemBuscado, estadoEnProgreso);
        var otraRevision = persistirRevision(otroItem, estadoEnProgreso);
        persistirObservacion(revisionBuscada);
        persistirObservacion(otraRevision);
        var estudiante = persistirEstudiante("Ivan Coral", "ivan@soyuco.edu.co");
        persistirVinculoEstudianteFicha(ficha, estudiante);
        entityManager.flush();

        var criteria = RevisionItemEstudianteCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("item", FiltroOperador.ES, itemBuscado.toString()))
                .build();

        // Act
        PaginatedResult<RevisionItemReadModel> resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).item()).isEqualTo(itemBuscado);
    }

    @Test
    void debeOrdenarPorEstadoRevisionNombre_cuandoCriteriaLoIndica() {
        // Arrange
        var asesor = persistirAsesor("Teresa Vanegas", "teresa@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto de Teresa");
        var itemUno = persistirItem(ficha, "Item en progreso");
        var itemDos = persistirItem(ficha, "Item cerrado");
        var revisionUno = persistirRevision(itemUno, estadoEnProgreso);
        var revisionDos = persistirRevision(itemDos, estadoCerrada);
        persistirObservacion(revisionUno);
        persistirObservacion(revisionDos);
        var estudiante = persistirEstudiante("Oscar Bravo", "oscar@soyuco.edu.co");
        persistirVinculoEstudianteFicha(ficha, estudiante);
        entityManager.flush();

        var criteria = RevisionItemEstudianteCriteria.builder()
                .pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("estadoRevision", SortDirection.DESC)))
                .build();

        // Act
        PaginatedResult<RevisionItemReadModel> resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(RevisionItemReadModel::estadoRevisionNombre)
                .containsExactly("En Progreso", "Cerrada");
    }

    @Test
    void debePaginar_segunPaginaYTamanio() {
        // Arrange
        var asesor = persistirAsesor("Ricardo Ariza", "ricardo@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto de Ricardo");
        var estudiante = persistirEstudiante("Silvia Nova", "silvia@soyuco.edu.co");
        persistirVinculoEstudianteFicha(ficha, estudiante);
        for (int i = 0; i < 3; i++) {
            var item = persistirItem(ficha, "Item " + i);
            var revision = persistirRevision(item, estadoEnProgreso);
            persistirObservacion(revision);
        }
        entityManager.flush();

        var criteria = RevisionItemEstudianteCriteria.builder().pagina(0).tamanio(2).build();

        // Act
        PaginatedResult<RevisionItemReadModel> resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getTotalElements()).isEqualTo(3L);
        assertThat(resultado.getPage()).isZero();
        assertThat(resultado.getSize()).isEqualTo(2);
    }

    private UUID persistirEstudiante(String nombre, String email) {
        var estudiante = EstudianteJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("EST-" + UUID.randomUUID().toString().substring(0, 8))
                .nombre(nombre)
                .email(email)
                .ocurridoEn(Instant.now())
                .build();
        entityManager.persist(estudiante);
        return estudiante.getId();
    }

    private void persistirVinculoEstudianteFicha(UUID fichaPerfilId, UUID estudianteId) {
        entityManager.persist(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .estudianteId(estudianteId)
                .build());
    }

    private void persistirObservacion(UUID revisionItemId) {
        entityManager.persist(ObservacionItemJpaEntity.builder()
                .id(UUID.randomUUID())
                .revisionItemId(revisionItemId)
                .observacion("Observacion de prueba")
                .estadoObservacionRevision(estadoPendiente)
                .build());
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
