package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.entity.EstadoEvaluacionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.entity.EstadoEvaluacionFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.entity.EvaluacionFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.entity.FichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.representantecomite.command.secondaryadapter.entity.RepresentanteComiteJpaEntity;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilObjeto;
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
import static org.assertj.core.api.Assertions.within;

@DataJpaTest
class EvaluacionFichaPerfilAsesorQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EvaluacionFichaPerfilQueryRepository repository;

    @Autowired
    private EvaluacionFichaPerfilEstudianteQueryRepository estudianteRepository;

    @Autowired
    private EvaluacionFichaPerfilCoordinadorQueryRepository coordinadorRepository;

    @Autowired
    private EvaluacionFichaPerfilAsesorQueryRepository asesorRepository;

    private EvaluacionFichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionFichaPerfilQueryOutputAdapter(
                repository,
                estudianteRepository,
                coordinadorRepository,
                asesorRepository,
                new EvaluacionFichaPerfilAsesorJpaSpecification());
        persistirEstado("EN_EVALUACION", "En Evaluación");
        persistirEstado("APROBADA", "Aprobada");
    }

    @Test
    void debeRetornarSoloEvaluacionesDeLasFichasDelAsesor_yExcluirLasDeOtroAsesor() {
        // Arrange
        var asesor = persistirAsesor("DOC-E01", "Asesor Uno", "asesor.uno@uco.edu.co");
        var otroAsesor = persistirAsesor("DOC-E02", "Asesor Dos", "asesor.dos@uco.edu.co");
        var fichaA = persistirFicha("Proyecto A", asesor);
        var fichaB = persistirFicha("Proyecto B", asesor);
        var fichaAjena = persistirFicha("Proyecto Ajeno", otroAsesor);
        var representante = persistirRepresentante("María Gómez", null);
        var evaluacionA = persistirEvaluacion(fichaA, representante, Instant.now());
        var evaluacionB = persistirEvaluacion(fichaB, representante, Instant.now());
        persistirEvaluacion(fichaAjena, representante, Instant.now());
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorAsesor(criteriaDeAsesor(asesor, null).build());

        // Assert
        assertThat(resultado.getContent()).extracting(EvaluacionFichaPerfilAsesorReadModel::id)
                .containsExactlyInAnyOrder(evaluacionA, evaluacionB);
        assertThat(resultado.getTotalElements()).isEqualTo(2L);
    }

    @Test
    void debeRetornarLaFilaCompletaConElEstadoActual_cuandoHayVariasTrazabilidades() {
        // Arrange
        var asesor = persistirAsesor("DOC-E03", "Asesor Tres", "asesor.tres@uco.edu.co");
        var ficha = persistirFicha("Sistema de gestion", asesor);
        var representante = persistirRepresentante("María Gómez", null);
        var fecha = Instant.now().minus(3, ChronoUnit.HOURS);
        var evaluacion = persistirEvaluacion(ficha, representante, fecha);
        persistirTrazabilidad(evaluacion, "EN_EVALUACION", fecha);
        persistirTrazabilidad(evaluacion, "APROBADA", fecha.plus(1, ChronoUnit.HOURS));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorAsesor(criteriaDeAsesor(asesor, null).build());

        // Assert
        assertThat(resultado.getContent()).singleElement().satisfies(e -> {
            assertThat(e.id()).isEqualTo(evaluacion);
            assertThat(e.fichaPerfil()).isEqualTo(ficha);
            assertThat(e.tituloProyecto()).isEqualTo("Sistema de gestion");
            assertThat(e.fechaCreacion()).isCloseTo(fecha, within(1, ChronoUnit.SECONDS));
            assertThat(e.estadoEvaluacion()).isEqualTo("APROBADA");
            assertThat(e.estadoEvaluacionNombre()).isEqualTo("Aprobada");
            assertThat(e.representanteComite().id()).isEqualTo(representante);
            assertThat(e.representanteComite().nombre()).isEqualTo("María Gómez");
        });
    }

    @Test
    void debeIncluirLaEvaluacion_cuandoElRepresentanteEstaDadoDeBaja() {
        // Arrange
        var asesor = persistirAsesor("DOC-E04", "Asesor Cuatro", "asesor.cuatro@uco.edu.co");
        var ficha = persistirFicha("Proyecto Baja", asesor);
        var representante = persistirRepresentante("Carlos Ruiz", Instant.now());
        var evaluacion = persistirEvaluacion(ficha, representante, Instant.now());
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorAsesor(criteriaDeAsesor(asesor, null).build());

        // Assert
        assertThat(resultado.getContent()).singleElement().satisfies(e -> {
            assertThat(e.id()).isEqualTo(evaluacion);
            assertThat(e.representanteComite().id()).isEqualTo(representante);
            assertThat(e.representanteComite().nombre()).isEqualTo("Carlos Ruiz");
        });
    }

    @Test
    void debeFiltrarPorFichaPerfil_combinandoConElForzadoPorAsesor() {
        // Arrange
        var asesor = persistirAsesor("DOC-E05", "Asesor Cinco", "asesor.cinco@uco.edu.co");
        var fichaA = persistirFicha("Proyecto A", asesor);
        var fichaB = persistirFicha("Proyecto B", asesor);
        var representante = persistirRepresentante("María Gómez", null);
        var evaluacionA = persistirEvaluacion(fichaA, representante, Instant.now());
        persistirEvaluacion(fichaB, representante, Instant.now());
        entityManager.flush();
        entityManager.clear();
        var filtro = NodoFiltro.predicado("fichaPerfil", FiltroOperador.ES, fichaA.toString());

        // Act
        var resultado = adapter.consultarPorAsesor(criteriaDeAsesor(asesor, filtro).build());

        // Assert
        assertThat(resultado.getContent()).extracting(EvaluacionFichaPerfilAsesorReadModel::id)
                .containsExactly(evaluacionA);
    }

    @Test
    void debeFiltrarPorTituloProyecto_combinandoConElForzadoPorAsesor() {
        // Arrange
        var asesor = persistirAsesor("DOC-E06", "Asesor Seis", "asesor.seis@uco.edu.co");
        var fichaA = persistirFicha("Sistema de gestion", asesor);
        var fichaB = persistirFicha("Otro proyecto", asesor);
        var representante = persistirRepresentante("María Gómez", null);
        var evaluacionA = persistirEvaluacion(fichaA, representante, Instant.now());
        persistirEvaluacion(fichaB, representante, Instant.now());
        entityManager.flush();
        entityManager.clear();
        var filtro = NodoFiltro.predicado("tituloProyecto", FiltroOperador.CONTIENE, "gestion");

        // Act
        var resultado = adapter.consultarPorAsesor(criteriaDeAsesor(asesor, filtro).build());

        // Assert
        assertThat(resultado.getContent()).extracting(EvaluacionFichaPerfilAsesorReadModel::id)
                .containsExactly(evaluacionA);
    }

    @Test
    void debeFiltrarPorEstadoActualDeLaEvaluacion_noPorUnEstadoHistorico() {
        // Arrange
        var asesor = persistirAsesor("DOC-E07", "Asesor Siete", "asesor.siete@uco.edu.co");
        var fichaA = persistirFicha("Proyecto A", asesor);
        var fichaB = persistirFicha("Proyecto B", asesor);
        var representante = persistirRepresentante("María Gómez", null);
        var base = Instant.now().minus(2, ChronoUnit.HOURS);
        var evaluacionAprobada = persistirEvaluacion(fichaA, representante, base);
        var evaluacionEnCurso = persistirEvaluacion(fichaB, representante, base);
        persistirTrazabilidad(evaluacionAprobada, "EN_EVALUACION", base);
        persistirTrazabilidad(evaluacionAprobada, "APROBADA", base.plus(1, ChronoUnit.HOURS));
        persistirTrazabilidad(evaluacionEnCurso, "EN_EVALUACION", base);
        entityManager.flush();
        entityManager.clear();
        var filtro = NodoFiltro.predicado("estadoEvaluacion", FiltroOperador.ES, "EN_EVALUACION");

        // Act
        var resultado = adapter.consultarPorAsesor(criteriaDeAsesor(asesor, filtro).build());

        // Assert
        assertThat(resultado.getContent()).extracting(EvaluacionFichaPerfilAsesorReadModel::id)
                .containsExactly(evaluacionEnCurso);
    }

    @Test
    void debeFiltrarPorRepresentanteComite_combinandoConElForzadoPorAsesor() {
        // Arrange
        var asesor = persistirAsesor("DOC-E08", "Asesor Ocho", "asesor.ocho@uco.edu.co");
        var ficha = persistirFicha("Proyecto A", asesor);
        var representanteUno = persistirRepresentante("María Gómez", null);
        var representanteDos = persistirRepresentante("Carlos Ruiz", null);
        var evaluacionUno = persistirEvaluacion(ficha, representanteUno, Instant.now());
        persistirEvaluacion(ficha, representanteDos, Instant.now());
        entityManager.flush();
        entityManager.clear();
        var filtro = NodoFiltro.predicado("representanteComite", FiltroOperador.ES, representanteUno.toString());

        // Act
        var resultado = adapter.consultarPorAsesor(criteriaDeAsesor(asesor, filtro).build());

        // Assert
        assertThat(resultado.getContent()).extracting(EvaluacionFichaPerfilAsesorReadModel::id)
                .containsExactly(evaluacionUno);
    }

    @Test
    void debeFiltrarPorFechaCreacion_combinandoConElForzadoPorAsesor() {
        // Arrange
        var asesor = persistirAsesor("DOC-E09", "Asesor Nueve", "asesor.nueve@uco.edu.co");
        var fichaA = persistirFicha("Proyecto A", asesor);
        var fichaB = persistirFicha("Proyecto B", asesor);
        var representante = persistirRepresentante("María Gómez", null);
        persistirEvaluacion(fichaA, representante, Instant.parse("2026-09-01T10:00:00Z"));
        var evaluacionReciente = persistirEvaluacion(fichaB, representante, Instant.parse("2026-09-10T10:00:00Z"));
        entityManager.flush();
        entityManager.clear();
        var filtro = NodoFiltro.predicado("fechaCreacion", FiltroOperador.MAYOR_QUE, "2026-09-05T00:00:00");

        // Act
        var resultado = adapter.consultarPorAsesor(criteriaDeAsesor(asesor, filtro).build());

        // Assert
        assertThat(resultado.getContent()).extracting(EvaluacionFichaPerfilAsesorReadModel::id)
                .containsExactly(evaluacionReciente);
    }

    @Test
    void debeOrdenarPorFechaCreacionDescendente() {
        // Arrange
        var asesor = persistirAsesor("DOC-E10", "Asesor Diez", "asesor.diez@uco.edu.co");
        var ficha = persistirFicha("Proyecto A", asesor);
        var base = Instant.now().minus(10, ChronoUnit.DAYS);
        var antigua = persistirEvaluacion(ficha, persistirRepresentante("María Gómez", null), base);
        var intermedia = persistirEvaluacion(ficha, persistirRepresentante("Carlos Ruiz", null),
                base.plus(2, ChronoUnit.DAYS));
        var reciente = persistirEvaluacion(ficha, persistirRepresentante("Ana Pérez", null),
                base.plus(5, ChronoUnit.DAYS));
        entityManager.flush();
        entityManager.clear();
        var criteria = criteriaDeAsesor(asesor, null)
                .ordenamiento(List.of(SortOrder.of("fechaCreacion", SortDirection.DESC)))
                .build();

        // Act
        var resultado = adapter.consultarPorAsesor(criteria);

        // Assert
        assertThat(resultado.getContent()).extracting(EvaluacionFichaPerfilAsesorReadModel::id)
                .containsExactly(reciente, intermedia, antigua);
    }

    @Test
    void debeOrdenarPorTituloProyectoAscendente() {
        // Arrange
        var asesor = persistirAsesor("DOC-E11", "Asesor Once", "asesor.once@uco.edu.co");
        var representante = persistirRepresentante("María Gómez", null);
        var evaluacionZeta = persistirEvaluacion(persistirFicha("Zeta", asesor), representante, Instant.now());
        var evaluacionAlfa = persistirEvaluacion(persistirFicha("Alfa", asesor), representante, Instant.now());
        var evaluacionMu = persistirEvaluacion(persistirFicha("Mu", asesor), representante, Instant.now());
        entityManager.flush();
        entityManager.clear();
        var criteria = criteriaDeAsesor(asesor, null)
                .ordenamiento(List.of(SortOrder.of("tituloProyecto", SortDirection.ASC)))
                .build();

        // Act
        var resultado = adapter.consultarPorAsesor(criteria);

        // Assert
        assertThat(resultado.getContent()).extracting(EvaluacionFichaPerfilAsesorReadModel::id)
                .containsExactly(evaluacionAlfa, evaluacionMu, evaluacionZeta);
    }

    @Test
    void debePaginarConElTotalDeElementos_cuandoHayMasEvaluacionesQueElTamanio() {
        // Arrange
        var asesor = persistirAsesor("DOC-E12", "Asesor Doce", "asesor.doce@uco.edu.co");
        var representante = persistirRepresentante("María Gómez", null);
        persistirEvaluacion(persistirFicha("Proyecto Uno", asesor), representante, Instant.now());
        persistirEvaluacion(persistirFicha("Proyecto Dos", asesor), representante, Instant.now());
        persistirEvaluacion(persistirFicha("Proyecto Tres", asesor), representante, Instant.now());
        entityManager.flush();
        entityManager.clear();
        var criteria = criteriaDeAsesor(asesor, null).tamanio(2).build();

        // Act
        var resultado = adapter.consultarPorAsesor(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getTotalElements()).isEqualTo(3L);
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElAsesorNoTieneFichas() {
        // Arrange
        var asesor = persistirAsesor("DOC-E13", "Asesor Trece", "asesor.trece@uco.edu.co");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorAsesor(criteriaDeAsesor(asesor, null).build());

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElFiltroDeFichaApuntaAUnaFichaDeOtroAsesor() {
        // Arrange
        var asesorA = persistirAsesor("DOC-E14", "Asesor A", "asesor.a@uco.edu.co");
        var asesorB = persistirAsesor("DOC-E15", "Asesor B", "asesor.b@uco.edu.co");
        var fichaDeB = persistirFicha("Proyecto de B", asesorB);
        persistirEvaluacion(fichaDeB, persistirRepresentante("María Gómez", null), Instant.now());
        entityManager.flush();
        entityManager.clear();
        var filtro = NodoFiltro.predicado("fichaPerfil", FiltroOperador.ES, fichaDeB.toString());

        // Act
        var resultado = adapter.consultarPorAsesor(criteriaDeAsesor(asesorA, filtro).build());

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    private EvaluacionFichaPerfilAsesorCriteria.Builder criteriaDeAsesor(UUID asesor, NodoFiltro filtroCliente) {
        var forzado = NodoFiltro.predicado("asesorFicha", FiltroOperador.ES, asesor.toString());
        var raiz = UtilObjeto.noEsNulo(filtroCliente)
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, filtroCliente))
                : forzado;
        return EvaluacionFichaPerfilAsesorCriteria.builder().pagina(0).tamanio(10).raiz(raiz);
    }

    private UUID persistirAsesor(String identificador, String nombre, String email) {
        var asesor = AsesorFichaJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .identificador(identificador)
                .nombre(nombre)
                .email(email)
                .ocurridoEn(Instant.now())
                .build();
        entityManager.persist(asesor);
        return asesor.getId();
    }

    private UUID persistirFicha(String titulo, UUID asesorId) {
        var asesorRef = entityManager.getEntityManager().getReference(AsesorFichaJpaEntity.class, asesorId);
        var ficha = FichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .tituloProyecto(titulo)
                .asesorFicha(asesorRef)
                .build();
        entityManager.persist(ficha);
        return ficha.getId();
    }

    private UUID persistirRepresentante(String nombre, Instant eliminadoEn) {
        var representante = RepresentanteComiteJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .identificador("RC-" + nombre.length())
                .nombre(nombre)
                .email("representante@uco.edu.co")
                .ocurridoEn(Instant.now())
                .eliminadoEn(eliminadoEn)
                .build();
        entityManager.persist(representante);
        return representante.getId();
    }

    private void persistirEstado(String id, String nombre) {
        entityManager.persist(EstadoEvaluacionJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion("Descripción de " + nombre)
                .build());
    }

    private UUID persistirEvaluacion(UUID fichaId, UUID representanteId, Instant fechaCreacion) {
        var evaluacion = EvaluacionFichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(fichaId)
                .representanteComiteId(representanteId)
                .fechaCreacion(fechaCreacion)
                .build();
        entityManager.persist(evaluacion);
        return evaluacion.getId();
    }

    private void persistirTrazabilidad(UUID evaluacionId, String estadoId, Instant fechaActualizacion) {
        var evaluacionRef = entityManager.getEntityManager()
                .getReference(EvaluacionFichaPerfilJpaEntity.class, evaluacionId);
        var estadoRef = entityManager.getEntityManager()
                .getReference(EstadoEvaluacionJpaEntity.class, estadoId);
        entityManager.persist(EstadoEvaluacionFichaJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionFichaPerfil(evaluacionRef)
                .estadoEvaluacion(estadoRef)
                .fechaActualizacion(fechaActualizacion)
                .build());
    }
}
