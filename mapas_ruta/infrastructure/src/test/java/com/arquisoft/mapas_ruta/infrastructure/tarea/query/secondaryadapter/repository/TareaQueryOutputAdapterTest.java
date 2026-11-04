package com.arquisoft.mapas_ruta.infrastructure.tarea.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.entity.MapaRutaJpaEntity;
import com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.entity.TareaJpaEntity;
import com.arquisoft.mapas_ruta.application.tarea.query.criteria.TareaMapaRutaCriteria;
import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.mapper.ConsultarTareasMapaRutaEstudianteMapper;
import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarTareasMapaRutaEstudianteQuery;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
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
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TareaQueryOutputAdapter.class, TareaJpaSpecification.class})
class TareaQueryOutputAdapterTest {

    private static final LocalDate FECHA_INICIO = LocalDate.of(2026, 10, 1);
    private static final LocalDate FECHA_FIN = LocalDate.of(2026, 12, 1);

    @Autowired
    private TareaQueryOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void crearTablasDeReplica() {
        var em = entityManager.getEntityManager();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS asesor (id UUID PRIMARY KEY, identificador VARCHAR(30), "
                + "nombre VARCHAR(50), email VARCHAR(50), eliminado_en TIMESTAMP WITH TIME ZONE)").executeUpdate();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS estado_item (id VARCHAR(60) PRIMARY KEY, "
                + "nombre VARCHAR(60), descripcion VARCHAR(300))").executeUpdate();
        em.createNativeQuery("MERGE INTO estado_item (id, nombre, descripcion) KEY (id) "
                + "VALUES ('EN_PROGRESO', 'En progreso', 'desc')").executeUpdate();
        em.createNativeQuery("MERGE INTO estado_item (id, nombre, descripcion) KEY (id) "
                + "VALUES ('NUEVA', 'Nueva', 'desc')").executeUpdate();
    }

    private UUID sembrarAsesor(String nombre, String email, boolean dadoDeBaja) {
        var id = UtilUUID.generarNuevoUUID();
        var baja = dadoDeBaja ? "CURRENT_TIMESTAMP" : "NULL";
        entityManager.getEntityManager().createNativeQuery("INSERT INTO asesor (id, identificador, nombre, email, "
                        + "eliminado_en) VALUES (?1, 'ident', ?2, ?3, " + baja + ")")
                .setParameter(1, id).setParameter(2, nombre).setParameter(3, email).executeUpdate();
        return id;
    }

    private UUID sembrarMapa() {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(MapaRutaJpaEntity.builder()
                .id(id)
                .proyectoGradoId(UtilUUID.generarNuevoUUID())
                .fechaInicio(FECHA_INICIO)
                .fechaFin(FECHA_FIN)
                .build());
        return id;
    }

    private UUID sembrarTarea(UUID mapa, UUID asesor, String descripcion) {
        return sembrarTarea(mapa, asesor, descripcion, "EN_PROGRESO");
    }

    private UUID sembrarTarea(UUID mapa, UUID asesor, String descripcion, String estado) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(TareaJpaEntity.builder()
                .id(id)
                .mapaRutaId(mapa)
                .descripcion(descripcion)
                .asesorId(asesor)
                .estadoItemId(estado)
                .build());
        return id;
    }

    private static TareaMapaRutaCriteria criteria(UUID mapa, int pagina, int tamanio,
                                                  List<SortOrder> orden, NodoFiltro raiz) {
        return ConsultarTareasMapaRutaEstudianteMapper.toCriteria(ConsultarTareasMapaRutaEstudianteQuery.crear(mapa,
                ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz)));
    }

    private static List<String> descripciones(List<TareaReadModel> tareas) {
        return tareas.stream().map(TareaReadModel::descripcion).toList();
    }

    @Test
    void debeConsultarSoloTareasDelMapa_cuandoHayVariosMapas() {
        // Arrange
        var mapa = sembrarMapa();
        var asesor = sembrarAsesor("Ana Perez", "ana@uco.edu.co", false);
        sembrarTarea(mapa, asesor, "Capitulo 1");
        sembrarTarea(mapa, asesor, "Capitulo 2");
        sembrarTarea(sembrarMapa(), asesor, "Ajena");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorMapaRuta(criteria(mapa, 0, 10, List.of(), null));

        // Assert
        assertThat(descripciones(resultado.getContent())).containsExactlyInAnyOrder("Capitulo 1", "Capitulo 2");
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getContent()).allSatisfy(readModel -> {
            assertThat(readModel.mapaRuta()).isEqualTo(mapa);
            assertThat(readModel.nombreAsesor()).isEqualTo("Ana Perez");
            assertThat(readModel.nombreEstadoItem()).isEqualTo("En progreso");
        });
    }

    @Test
    void debeFiltrarPorDescripcion_cuandoElTextoCoincide() {
        // Arrange
        var mapa = sembrarMapa();
        var asesor = sembrarAsesor("Ana Perez", "ana@uco.edu.co", false);
        sembrarTarea(mapa, asesor, "Redactar capitulo");
        sembrarTarea(mapa, asesor, "Revisar bibliografia");
        entityManager.flush();
        entityManager.clear();
        var raiz = NodoFiltro.predicado("descripcion", FiltroOperador.CONTIENE, "bibliografia");

        // Act
        var resultado = adapter.consultarPorMapaRuta(criteria(mapa, 0, 10, List.of(), raiz));

        // Assert
        assertThat(descripciones(resultado.getContent())).containsExactly("Revisar bibliografia");
    }

    @Test
    void debeFiltrarPorEstadoItem_cuandoElEstadoCoincide() {
        // Arrange
        var mapa = sembrarMapa();
        var asesor = sembrarAsesor("Ana Perez", "ana@uco.edu.co", false);
        sembrarTarea(mapa, asesor, "En curso", "EN_PROGRESO");
        sembrarTarea(mapa, asesor, "Pendiente", "NUEVA");
        entityManager.flush();
        entityManager.clear();
        var raiz = NodoFiltro.predicado("estadoItem", FiltroOperador.ES, "NUEVA");

        // Act
        var resultado = adapter.consultarPorMapaRuta(criteria(mapa, 0, 10, List.of(), raiz));

        // Assert
        assertThat(descripciones(resultado.getContent())).containsExactly("Pendiente");
    }

    @Test
    void debeFiltrarPorAsesor_cuandoElAsesorCoincide() {
        // Arrange
        var mapa = sembrarMapa();
        var ana = sembrarAsesor("Ana Perez", "ana@uco.edu.co", false);
        var luis = sembrarAsesor("Luis Gomez", "luis@uco.edu.co", false);
        sembrarTarea(mapa, ana, "De Ana");
        sembrarTarea(mapa, luis, "De Luis");
        entityManager.flush();
        entityManager.clear();
        var raiz = NodoFiltro.predicado("asesor", FiltroOperador.ES, luis.toString());

        // Act
        var resultado = adapter.consultarPorMapaRuta(criteria(mapa, 0, 10, List.of(), raiz));

        // Assert
        assertThat(descripciones(resultado.getContent())).containsExactly("De Luis");
    }

    @Test
    void debeOrdenarPorDescripcion_cuandoSeSolicitaDescendente() {
        // Arrange
        var mapa = sembrarMapa();
        var asesor = sembrarAsesor("Ana Perez", "ana@uco.edu.co", false);
        sembrarTarea(mapa, asesor, "B tarea");
        sembrarTarea(mapa, asesor, "A tarea");
        sembrarTarea(mapa, asesor, "C tarea");
        entityManager.flush();
        entityManager.clear();
        var orden = List.of(SortOrder.of("descripcion", SortDirection.DESC));

        // Act
        var resultado = adapter.consultarPorMapaRuta(criteria(mapa, 0, 10, orden, null));

        // Assert
        assertThat(descripciones(resultado.getContent())).containsExactly("C tarea", "B tarea", "A tarea");
    }

    @Test
    void debePaginar_cuandoHayMasTareasQueElTamanio() {
        // Arrange
        var mapa = sembrarMapa();
        var asesor = sembrarAsesor("Ana Perez", "ana@uco.edu.co", false);
        sembrarTarea(mapa, asesor, "Tarea 1");
        sembrarTarea(mapa, asesor, "Tarea 2");
        sembrarTarea(mapa, asesor, "Tarea 3");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorMapaRuta(criteria(mapa, 1, 2, List.of(), null));

        // Assert
        assertThat(descripciones(resultado.getContent())).containsExactly("Tarea 3");
        assertThat(resultado.getTotalElements()).isEqualTo(3);
        assertThat(resultado.getTotalPages()).isEqualTo(2);
        assertThat(resultado.getPage()).isEqualTo(1);
    }

    @Test
    void debeDevolverPaginaVacia_cuandoElMapaNoTieneTareas() {
        // Arrange
        sembrarTarea(sembrarMapa(), sembrarAsesor("Ana Perez", "ana@uco.edu.co", false), "Ajena");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorMapaRuta(criteria(UtilUUID.generarNuevoUUID(), 0, 10, List.of(), null));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeDevolverElDetalleCompleto_cuandoLaTareaExiste() {
        // Arrange
        var mapa = sembrarMapa();
        var asesor = sembrarAsesor("Ana Perez", "ana@uco.edu.co", false);
        var tarea = sembrarTarea(mapa, asesor, "Redactar capitulo");
        sembrarTarea(mapa, asesor, "Otra tarea");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorId(tarea);

        // Assert
        assertThat(resultado).hasValueSatisfying(readModel -> {
            assertThat(readModel.id()).isEqualTo(tarea);
            assertThat(readModel.descripcion()).isEqualTo("Redactar capitulo");
            assertThat(readModel.mapaRuta()).isEqualTo(mapa);
            assertThat(readModel.fechaInicioMapaRuta()).isEqualTo(FECHA_INICIO);
            assertThat(readModel.fechaFinMapaRuta()).isEqualTo(FECHA_FIN);
            assertThat(readModel.estadoItem()).isEqualTo("EN_PROGRESO");
            assertThat(readModel.nombreEstadoItem()).isEqualTo("En progreso");
            assertThat(readModel.asesor()).isEqualTo(asesor);
            assertThat(readModel.nombreAsesor()).isEqualTo("Ana Perez");
            assertThat(readModel.emailAsesor()).isEqualTo("ana@uco.edu.co");
        });
    }

    @Test
    void debeDevolverVacio_cuandoLaTareaNoExiste() {
        // Arrange
        sembrarTarea(sembrarMapa(), sembrarAsesor("Ana Perez", "ana@uco.edu.co", false), "Redactar capitulo");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeDevolverLaTarea_cuandoElAsesorTieneEliminadoEn() {
        // Arrange
        var asesor = sembrarAsesor("Luis Gomez", "luis@uco.edu.co", true);
        var tarea = sembrarTarea(sembrarMapa(), asesor, "Revisar bibliografia");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorId(tarea);

        // Assert
        assertThat(resultado).hasValueSatisfying(readModel -> {
            assertThat(readModel.asesor()).isEqualTo(asesor);
            assertThat(readModel.nombreAsesor()).isEqualTo("Luis Gomez");
        });
    }
}
