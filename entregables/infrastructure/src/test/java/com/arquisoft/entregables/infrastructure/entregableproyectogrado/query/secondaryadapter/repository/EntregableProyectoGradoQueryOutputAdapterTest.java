package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;
import com.arquisoft.entregables.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.entregables.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
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
class EntregableProyectoGradoQueryOutputAdapterTest {

    @Autowired
    private EntregableProyectoGradoQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private EntregableProyectoGradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EntregableProyectoGradoQueryOutputAdapter(repository, new EntregableProyectoGradoJpaSpecification());
        var em = entityManager.getEntityManager();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS estado_entregable (id VARCHAR(60) PRIMARY KEY, "
                + "nombre VARCHAR(60), descripcion VARCHAR(300))").executeUpdate();
        em.createNativeQuery("DELETE FROM estado_entregable").executeUpdate();
        sembrarEstado("DISPONIBLE_EVALUACION", "Disponible Evaluacion");
        sembrarEstado("APROBADO", "Aprobado");
        sembrarEstado("CERRADO", "Cerrado");
    }

    private void sembrarEstado(String id, String nombre) {
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO estado_entregable (id, nombre, descripcion) VALUES (?1, ?2, 'd')")
                .setParameter(1, id).setParameter(2, nombre)
                .executeUpdate();
    }

    private UUID sembrarProyecto(String nombre) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(id).nombre(nombre).build());
        return id;
    }

    private UUID sembrarAsesor(String nombre, Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(AsesorJpaEntity.builder()
                .id(id).identificador("ID-" + nombre).nombre(nombre).email(nombre + "@uco.edu.co")
                .ocurridoEn(Instant.parse("2026-01-01T00:00:00Z")).eliminadoEn(eliminadoEn).build());
        return id;
    }

    private UUID sembrarEntregable(UUID proyecto, int version, UUID asesor, String estado) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new EntregableProyectoGradoJpaEntity(id, proyecto, version, asesor, estado));
        return id;
    }

    private void confirmar() {
        entityManager.flush();
        entityManager.clear();
    }

    private static EntregableProyectoGradoCriteria criteria(NodoFiltro raiz) {
        return EntregableProyectoGradoCriteria.builder().pagina(0).tamanio(10).raiz(raiz).build();
    }

    @Test
    void debeRetornarTodosConNombresDeProyectoAsesorYEstado_cuandoNoHayFiltros() {
        // Arrange
        var proyecto = sembrarProyecto("Proyecto Alfa");
        var asesor = sembrarAsesor("Ana", null);
        var id = sembrarEntregable(proyecto, 3, asesor, "APROBADO");
        sembrarEntregable(proyecto, 4, asesor, "CERRADO");
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(null));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(2L);
        var leido = resultado.getContent().stream().filter(e -> e.id().equals(id)).findFirst().orElseThrow();
        assertThat(leido.versionEntregable()).isEqualTo(3);
        assertThat(leido.proyectoGrado().id()).isEqualTo(proyecto);
        assertThat(leido.proyectoGrado().nombre()).isEqualTo("Proyecto Alfa");
        assertThat(leido.asesor().id()).isEqualTo(asesor);
        assertThat(leido.asesor().nombre()).isEqualTo("Ana");
        assertThat(leido.asesor().email()).isEqualTo("Ana@uco.edu.co");
        assertThat(leido.estadoEntregable().id()).isEqualTo("APROBADO");
        assertThat(leido.estadoEntregable().nombre()).isEqualTo("Aprobado");
    }

    @Test
    void debeFiltrarPorProyectoGrado() {
        // Arrange
        var proyectoA = sembrarProyecto("A");
        var proyectoB = sembrarProyecto("B");
        var asesor = sembrarAsesor("Ana", null);
        var esperado = sembrarEntregable(proyectoA, 1, asesor, "APROBADO");
        sembrarEntregable(proyectoB, 1, asesor, "APROBADO");
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(
                NodoFiltro.predicado("proyectoGradoId", FiltroOperador.ES, proyectoA.toString())));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactly(esperado);
    }

    @Test
    void debeFiltrarPorAsesor() {
        // Arrange
        var proyecto = sembrarProyecto("A");
        var ana = sembrarAsesor("Ana", null);
        var luis = sembrarAsesor("Luis", null);
        var esperado = sembrarEntregable(proyecto, 1, ana, "APROBADO");
        sembrarEntregable(proyecto, 2, luis, "APROBADO");
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(
                NodoFiltro.predicado("asesorId", FiltroOperador.ES, ana.toString())));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactly(esperado);
    }

    @Test
    void debeFiltrarPorEstado() {
        // Arrange
        var proyecto = sembrarProyecto("A");
        var asesor = sembrarAsesor("Ana", null);
        var esperado = sembrarEntregable(proyecto, 1, asesor, "APROBADO");
        sembrarEntregable(proyecto, 2, asesor, "CERRADO");
        sembrarEntregable(proyecto, 3, asesor, "DISPONIBLE_EVALUACION");
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(
                NodoFiltro.predicado("estadoEntregable", FiltroOperador.ES, "APROBADO")));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactly(esperado);
    }

    @Test
    void debeCombinarFiltrosConAnd() {
        // Arrange
        var proyecto = sembrarProyecto("A");
        var ana = sembrarAsesor("Ana", null);
        var luis = sembrarAsesor("Luis", null);
        var esperado = sembrarEntregable(proyecto, 1, ana, "APROBADO");
        sembrarEntregable(proyecto, 2, ana, "CERRADO");
        sembrarEntregable(proyecto, 3, luis, "APROBADO");
        confirmar();
        var raiz = NodoFiltro.grupo(FiltroConector.AND, List.of(
                NodoFiltro.predicado("asesorId", FiltroOperador.ES, ana.toString()),
                NodoFiltro.predicado("estadoEntregable", FiltroOperador.ES, "APROBADO")));

        // Act
        var resultado = adapter.consultar(criteria(raiz));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactly(esperado);
    }

    @Test
    void debeCombinarFiltrosConOr() {
        // Arrange
        var proyecto = sembrarProyecto("A");
        var asesor = sembrarAsesor("Ana", null);
        var aprobado = sembrarEntregable(proyecto, 1, asesor, "APROBADO");
        var cerrado = sembrarEntregable(proyecto, 2, asesor, "CERRADO");
        sembrarEntregable(proyecto, 3, asesor, "DISPONIBLE_EVALUACION");
        confirmar();
        var raiz = NodoFiltro.predicadoMultivalor("estadoEntregable", FiltroOperador.IN, List.of("APROBADO", "CERRADO"));

        // Act
        var resultado = adapter.consultar(criteria(raiz));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactlyInAnyOrder(aprobado, cerrado);
    }

    @Test
    void debeFiltrarPorVersion() {
        // Arrange
        var proyecto = sembrarProyecto("A");
        var asesor = sembrarAsesor("Ana", null);
        sembrarEntregable(proyecto, 1, asesor, "APROBADO");
        var esperado = sembrarEntregable(proyecto, 2, asesor, "APROBADO");
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(
                NodoFiltro.predicado("versionEntregable", FiltroOperador.ES, "2")));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactly(esperado);
    }

    @Test
    void debeOrdenarPorVersionDescendente() {
        // Arrange
        var proyecto = sembrarProyecto("A");
        var asesor = sembrarAsesor("Ana", null);
        sembrarEntregable(proyecto, 1, asesor, "APROBADO");
        sembrarEntregable(proyecto, 3, asesor, "APROBADO");
        sembrarEntregable(proyecto, 2, asesor, "APROBADO");
        confirmar();

        // Act
        var resultado = adapter.consultar(ordenado("versionEntregable", SortDirection.DESC));

        // Assert
        assertThat(resultado.getContent()).extracting("versionEntregable").containsExactly(3, 2, 1);
    }

    @Test
    void debeOrdenarPorNombreDeProyecto() {
        // Arrange
        var asesor = sembrarAsesor("Ana", null);
        sembrarEntregable(sembrarProyecto("Zeta"), 1, asesor, "APROBADO");
        sembrarEntregable(sembrarProyecto("Alfa"), 1, asesor, "APROBADO");
        confirmar();

        // Act
        var resultado = adapter.consultar(ordenado("proyectoGradoNombre", SortDirection.ASC));

        // Assert
        assertThat(resultado.getContent()).extracting(e -> e.proyectoGrado().nombre()).containsExactly("Alfa", "Zeta");
    }

    @Test
    void debeOrdenarPorNombreDeAsesor() {
        // Arrange
        var proyecto = sembrarProyecto("A");
        sembrarEntregable(proyecto, 1, sembrarAsesor("Luis", null), "APROBADO");
        sembrarEntregable(proyecto, 2, sembrarAsesor("Ana", null), "APROBADO");
        confirmar();

        // Act
        var resultado = adapter.consultar(ordenado("asesorNombre", SortDirection.DESC));

        // Assert
        assertThat(resultado.getContent()).extracting(e -> e.asesor().nombre()).containsExactly("Luis", "Ana");
    }

    @Test
    void debePaginar_yDevolverElTotal() {
        // Arrange
        var proyecto = sembrarProyecto("A");
        var asesor = sembrarAsesor("Ana", null);
        for (var version = 1; version <= 5; version++) {
            sembrarEntregable(proyecto, version, asesor, "APROBADO");
        }
        confirmar();
        var pedido = EntregableProyectoGradoCriteria.builder().pagina(1).tamanio(2)
                .ordenamiento(List.of(SortOrder.of("versionEntregable", SortDirection.ASC))).build();

        // Act
        var resultado = adapter.consultar(pedido);

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(5L);
        assertThat(resultado.getTotalPages()).isEqualTo(3);
        assertThat(resultado.getPage()).isEqualTo(1);
        assertThat(resultado.getContent()).extracting("versionEntregable").containsExactly(3, 4);
    }

    @Test
    void debeOrdenarPorIdPorDefecto_cuandoNoHayOrden() {
        // Arrange
        var proyecto = sembrarProyecto("A");
        var asesor = sembrarAsesor("Ana", null);
        for (var version = 1; version <= 4; version++) {
            sembrarEntregable(proyecto, version, asesor, "APROBADO");
        }
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(null));

        // Assert
        var ids = resultado.getContent().stream().map(e -> e.id().toString()).toList();
        assertThat(ids).isSorted();
    }

    @Test
    void debeIncluirElEntregable_cuandoElAsesorEstaDadoDeBaja() {
        // Arrange
        var proyecto = sembrarProyecto("A");
        var asesor = sembrarAsesor("Ana", Instant.parse("2026-09-24T10:00:00Z"));
        var id = sembrarEntregable(proyecto, 1, asesor, "APROBADO");
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(null));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactly(id);
        assertThat(resultado.getContent().get(0).asesor().nombre()).isEqualTo("Ana");
    }

    @Test
    void debeRetornarPaginaVacia_cuandoNingunoCoincide() {
        // Arrange
        var proyecto = sembrarProyecto("A");
        sembrarEntregable(proyecto, 1, sembrarAsesor("Ana", null), "APROBADO");
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(
                NodoFiltro.predicado("estadoEntregable", FiltroOperador.ES, "CERRADO")));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    private static EntregableProyectoGradoCriteria ordenado(String campo, SortDirection direccion) {
        return EntregableProyectoGradoCriteria.builder().pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of(campo, direccion))).build();
    }
}
