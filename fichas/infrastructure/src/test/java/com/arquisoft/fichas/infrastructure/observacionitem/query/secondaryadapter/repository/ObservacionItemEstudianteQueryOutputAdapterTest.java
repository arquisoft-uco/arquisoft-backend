package com.arquisoft.fichas.infrastructure.observacionitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemEstudianteCriteria;
import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
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
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilObjeto;
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
class ObservacionItemEstudianteQueryOutputAdapterTest {

    private static final int ORDEN_POR_DEFECTO_DEL_CASE = 4;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ObservacionItemQueryRepository observacionItemRepository;

    @Autowired
    private ObservacionItemEstudianteQueryRepository observacionItemEstudianteRepository;

    private ObservacionItemQueryOutputAdapter adapter;

    private EstadoObservacionRevisionJpaEntity pendiente;
    private EstadoObservacionRevisionJpaEntity enProgreso;
    private EstadoObservacionRevisionJpaEntity cerrado;
    private EstadoRevisionJpaEntity estadoRevision;
    private TipoItemJpaEntity tipoItem;

    @BeforeEach
    void setUp() {
        adapter = new ObservacionItemQueryOutputAdapter(
                observacionItemRepository, new ObservacionItemJpaSpecification(),
                observacionItemEstudianteRepository, new ObservacionItemEstudianteJpaSpecification());

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
    void debeRetornarPaginaVacia_cuandoElEstudianteNoTieneObservacionesEnBD() {
        // Arrange
        var estudiante = persistirEstudiante("Sin Observaciones", "sinobs@soyuco.edu.co");
        entityManager.flush();
        var criteria = criteriaDeEstudiante(estudiante, null).build();

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeRetornarSoloObservacionesDeSusFichas_yResolverAliasDelSubselect() {
        // Arrange
        var asesor = persistirAsesor("Carla Diaz", "carla@soyuco.edu.co");
        var estudiante = persistirEstudiante("Pedro Lara", "pedro@soyuco.edu.co");
        var ajeno = persistirEstudiante("Camila Ruiz", "camila@soyuco.edu.co");
        var fichaPropia = persistirFicha(asesor, "Proyecto propio");
        var fichaAjena = persistirFicha(asesor, "Proyecto ajeno");
        persistirVinculoEstudianteFicha(fichaPropia, estudiante);
        persistirVinculoEstudianteFicha(fichaAjena, ajeno);
        var revisionPropia = persistirRevisionDeNuevoItem(fichaPropia);
        var revisionAjena = persistirRevisionDeNuevoItem(fichaAjena);
        var propia = persistirObservacion(revisionPropia, "Observacion de mi ficha", enProgreso);
        persistirObservacion(revisionAjena, "Observacion de ficha ajena", pendiente);
        entityManager.flush();
        var criteria = criteriaDeEstudiante(estudiante, null).build();

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
        assertThat(resultado.getContent()).singleElement().satisfies(observacion -> {
            assertThat(observacion.id()).isEqualTo(propia);
            assertThat(observacion.revisionItem()).isEqualTo(revisionPropia);
            assertThat(observacion.observacion()).isEqualTo("Observacion de mi ficha");
            assertThat(observacion.estadoObservacionRevision()).isEqualTo("EN_PROGRESO");
            assertThat(observacion.estadoObservacionRevisionNombre()).isEqualTo("En Progreso");
        });
    }

    @Test
    void debeRetornarObservacionesDeTodasLasFichasVinculadas_cuandoElEstudianteTieneVarias() {
        // Arrange
        var asesor = persistirAsesor("Ana Ramirez", "ana@soyuco.edu.co");
        var estudiante = persistirEstudiante("Nadia Rios", "nadia@soyuco.edu.co");
        var fichaUno = persistirFicha(asesor, "Proyecto uno");
        var fichaDos = persistirFicha(asesor, "Proyecto dos");
        persistirVinculoEstudianteFicha(fichaUno, estudiante);
        persistirVinculoEstudianteFicha(fichaDos, estudiante);
        var enFichaUno = persistirObservacion(persistirRevisionDeNuevoItem(fichaUno), "Observacion uno", pendiente);
        var enFichaDos = persistirObservacion(persistirRevisionDeNuevoItem(fichaDos), "Observacion dos", cerrado);
        entityManager.flush();
        var criteria = criteriaDeEstudiante(estudiante, null).build();

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(ObservacionItemReadModel::id)
                .containsExactlyInAnyOrder(enFichaUno, enFichaDos);
    }

    @Test
    void debeRetornarCadaObservacionUnaSolaVez_cuandoLaFichaTieneVariosEstudiantesVinculados() {
        // Arrange
        var asesor = persistirAsesor("Juan Salazar", "juan@soyuco.edu.co");
        var estudianteUno = persistirEstudiante("Estudiante Uno", "uno@soyuco.edu.co");
        var estudianteDos = persistirEstudiante("Estudiante Dos", "dos@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto compartido");
        persistirVinculoEstudianteFicha(ficha, estudianteUno);
        persistirVinculoEstudianteFicha(ficha, estudianteDos);
        var revision = persistirRevisionDeNuevoItem(ficha);
        var compartida = persistirObservacion(revision, "Observacion compartida", pendiente);
        entityManager.flush();

        // Act
        var paraUno = adapter.consultarTodasEstudiante(criteriaDeEstudiante(estudianteUno, null).build());
        var paraDos = adapter.consultarTodasEstudiante(criteriaDeEstudiante(estudianteDos, null).build());

        // Assert
        assertThat(paraUno.getContent()).extracting(ObservacionItemReadModel::id).containsExactly(compartida);
        assertThat(paraDos.getContent()).extracting(ObservacionItemReadModel::id).containsExactly(compartida);
        assertThat(paraUno.getTotalElements()).isEqualTo(1L);
        assertThat(paraDos.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void debeFiltrarPorRevisionItem_cuandoLaRevisionEsDeUnaFichaDelEstudiante() {
        // Arrange
        var asesor = persistirAsesor("Marco Vidal", "marco@soyuco.edu.co");
        var estudiante = persistirEstudiante("Ivan Coral", "ivan@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto de Ivan");
        persistirVinculoEstudianteFicha(ficha, estudiante);
        var revisionBuscada = persistirRevisionDeNuevoItem(ficha);
        var otraRevision = persistirRevisionDeNuevoItem(ficha);
        var buscada = persistirObservacion(revisionBuscada, "Observacion buscada", pendiente);
        persistirObservacion(otraRevision, "Observacion de otra revision", pendiente);
        entityManager.flush();
        var criteria = criteriaDeEstudiante(estudiante,
                NodoFiltro.predicado("revisionItem", FiltroOperador.ES, revisionBuscada.toString())).build();

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getContent()).extracting(ObservacionItemReadModel::id).containsExactly(buscada);
    }

    @Test
    void debeRetornarPaginaVacia_cuandoFiltraPorRevisionItemDeUnaFichaAjena() {
        // Arrange
        var asesor = persistirAsesor("Olga Mejia", "olga@soyuco.edu.co");
        var estudiante = persistirEstudiante("Oscar Bravo", "oscar@soyuco.edu.co");
        var ajeno = persistirEstudiante("Silvia Nova", "silvia@soyuco.edu.co");
        var fichaPropia = persistirFicha(asesor, "Proyecto propio");
        var fichaAjena = persistirFicha(asesor, "Proyecto ajeno");
        persistirVinculoEstudianteFicha(fichaPropia, estudiante);
        persistirVinculoEstudianteFicha(fichaAjena, ajeno);
        persistirObservacion(persistirRevisionDeNuevoItem(fichaPropia), "Observacion propia", pendiente);
        var revisionAjena = persistirRevisionDeNuevoItem(fichaAjena);
        persistirObservacion(revisionAjena, "Observacion ajena", pendiente);
        entityManager.flush();
        var criteria = criteriaDeEstudiante(estudiante,
                NodoFiltro.predicado("revisionItem", FiltroOperador.ES, revisionAjena.toString())).build();

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElClienteMandaOtroEstudianteIdEnANDConElForzado() {
        // Arrange
        var asesor = persistirAsesor("Zulma Torres", "zulma@soyuco.edu.co");
        var estudiante = persistirEstudiante("Lucia Pardo", "lucia@soyuco.edu.co");
        var ajeno = persistirEstudiante("Hugo Mora", "hugo@soyuco.edu.co");
        var fichaPropia = persistirFicha(asesor, "Proyecto de Lucia");
        var fichaAjena = persistirFicha(asesor, "Proyecto de Hugo");
        persistirVinculoEstudianteFicha(fichaPropia, estudiante);
        persistirVinculoEstudianteFicha(fichaAjena, ajeno);
        persistirObservacion(persistirRevisionDeNuevoItem(fichaPropia), "Observacion propia", pendiente);
        persistirObservacion(persistirRevisionDeNuevoItem(fichaAjena), "Observacion ajena", pendiente);
        entityManager.flush();
        var criteria = criteriaDeEstudiante(estudiante,
                NodoFiltro.predicado("estudianteId", FiltroOperador.ES, ajeno.toString())).build();

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
    }

    @Test
    void debeFiltrarPorEstadoObservacionRevision_yExcluirLasDeOtrosEstados() {
        // Arrange
        var estudiante = sembrarUnaObservacionPorEstadoParaUnEstudiante();
        var criteria = criteriaDeEstudiante(estudiante,
                NodoFiltro.predicado("estadoObservacionRevision", FiltroOperador.ES, "EN_PROGRESO")).build();

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getContent()).singleElement().satisfies(observacion -> {
            assertThat(observacion.estadoObservacionRevision()).isEqualTo("EN_PROGRESO");
            assertThat(observacion.estadoObservacionRevisionNombre()).isEqualTo("En Progreso");
        });
    }

    @Test
    void debeOrdenarPorCicloDeVidaAscendente_cuandoElEstudianteOrdenaPorEstadoAsc() {
        // Arrange
        var estudiante = sembrarUnaObservacionPorEstadoParaUnEstudiante();
        var criteria = criteriaDeEstudiante(estudiante, null)
                .ordenamiento(List.of(SortOrder.of("estadoObservacionRevision", SortDirection.ASC)))
                .build();

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(ObservacionItemReadModel::estadoObservacionRevision)
                .containsExactly("PENDIENTE", "EN_PROGRESO", "CERRADO");
    }

    @Test
    void debeOrdenarPorCicloDeVidaDescendente_cuandoElEstudianteOrdenaPorEstadoDesc() {
        // Arrange
        var estudiante = sembrarUnaObservacionPorEstadoParaUnEstudiante();
        var criteria = criteriaDeEstudiante(estudiante, null)
                .ordenamiento(List.of(SortOrder.of("estadoObservacionRevision", SortDirection.DESC)))
                .build();

        // Act
        var resultado = adapter.consultarTodasEstudiante(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(ObservacionItemReadModel::estadoObservacionRevision)
                .containsExactly("CERRADO", "EN_PROGRESO", "PENDIENTE");
    }

    @Test
    void debePaginarSinRepetirNiPerderFilas_yDesempatarPorId_cuandoElEstudianteTieneEmpatesDeEstado() {
        // Arrange
        var asesor = persistirAsesor("Nora Prieto", "nora@soyuco.edu.co");
        var estudiante = persistirEstudiante("Rafael Ortiz", "rafael@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto de Rafael");
        persistirVinculoEstudianteFicha(ficha, estudiante);
        var revision = persistirRevisionDeNuevoItem(ficha);
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
            pagina = adapter.consultarTodasEstudiante(ObservacionItemEstudianteCriteria.builder()
                    .pagina(numeroPagina).tamanio(2)
                    .ordenamiento(List.of(SortOrder.of("estadoObservacionRevision", SortDirection.ASC)))
                    .raiz(forzadoPorEstudiante(estudiante))
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
    void debeAsignarUnOrdenPropioAcadaEstadoDelDominio_enLaVistaDelEstudiante() {
        // Arrange
        var asesor = persistirAsesor("Paula Nieto", "paula@soyuco.edu.co");
        var estudiante = persistirEstudiante("Tomas Gil", "tomas@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto de Tomas");
        persistirVinculoEstudianteFicha(ficha, estudiante);
        var revision = persistirRevisionDeNuevoItem(ficha);
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
        var ordenes = observacionItemEstudianteRepository.findAll().stream()
                .map(ObservacionItemEstudianteJpaQueryEntity::getEstadoOrden)
                .toList();

        // Assert
        assertThat(ordenes)
                .hasSize(EstadoObservacionRevision.values().length)
                .doesNotHaveDuplicates()
                .doesNotContain(ORDEN_POR_DEFECTO_DEL_CASE);
    }

    private UUID sembrarUnaObservacionPorEstadoParaUnEstudiante() {
        var asesor = persistirAsesor("Elena Cruz", "elena@soyuco.edu.co");
        var estudiante = persistirEstudiante("Diego Rojas", "diego@soyuco.edu.co");
        var ficha = persistirFicha(asesor, "Proyecto de Diego");
        persistirVinculoEstudianteFicha(ficha, estudiante);
        var revision = persistirRevisionDeNuevoItem(ficha);
        persistirObservacion(revision, "Observacion cerrada", cerrado);
        persistirObservacion(revision, "Observacion pendiente", pendiente);
        persistirObservacion(revision, "Observacion en progreso", enProgreso);
        entityManager.flush();
        return estudiante;
    }

    private static NodoFiltro forzadoPorEstudiante(UUID estudiante) {
        return NodoFiltro.predicado("estudianteId", FiltroOperador.ES, estudiante.toString());
    }

    private static ObservacionItemEstudianteCriteria.Builder criteriaDeEstudiante(UUID estudiante, NodoFiltro filtro) {
        var forzado = forzadoPorEstudiante(estudiante);
        var raiz = UtilObjeto.esNulo(filtro)
                ? forzado
                : NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, filtro));
        return ObservacionItemEstudianteCriteria.builder().pagina(0).tamanio(10).raiz(raiz);
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
