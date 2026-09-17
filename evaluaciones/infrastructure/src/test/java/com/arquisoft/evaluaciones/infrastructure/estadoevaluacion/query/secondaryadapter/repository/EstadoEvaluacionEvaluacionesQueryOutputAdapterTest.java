package com.arquisoft.evaluaciones.infrastructure.estadoevaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// La tabla estado_evaluacion no tiene @Entity de escritura en evaluaciones (esta HU es solo
// lectura y el plan prohíbe crear un lado command/ para esta feature), así que se crea aquí con
// el mismo DDL de la migración V20260906143120 para poder ejercitar el @Subselect real, tal como
// exige la skill de testing.
@DataJpaTest
class EstadoEvaluacionEvaluacionesQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EstadoEvaluacionEvaluacionesQueryRepository repository;

    private EstadoEvaluacionEvaluacionesQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoEvaluacionEvaluacionesQueryOutputAdapter(repository);
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS estado_evaluacion (
                    id VARCHAR(60) NOT NULL,
                    nombre VARCHAR(60) NOT NULL,
                    descripcion VARCHAR(300) NOT NULL,
                    PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.flush();
    }

    private void sembrarEstado(String id, String nombre, String descripcion) {
        entityManager.createNativeQuery(
                        "INSERT INTO estado_evaluacion (id, nombre, descripcion) VALUES (?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, nombre)
                .setParameter(3, descripcion)
                .executeUpdate();
    }

    // El plan prohíbe explícitamente un ORDER BY nombre (rompería la secuencia lógica de negocio
    // PENDIENTE -> EN_PROGRESO -> FINALIZADA, ya que alfabéticamente "En progreso" < "Finalizada" <
    // "Pendiente"), confiando en cambio en el orden de insercion de la migracion V20260906143120.
    // Este test comprueba justo esa confianza: el PK (id) ordena alfabeticamente distinto al orden
    // de negocio ("EN_PROGRESO" < "FINALIZADA" < "PENDIENTE"), asi que si el motor devolviera las
    // filas por el indice de la PK en vez de por orden de insercion (o alguien reintrodujera un
    // ORDER BY nombre), containsExactly fallaria aqui y detectaria la regresion antes de produccion.
    // Verificado empiricamente: con H2 (MVStore) un SELECT sin ORDER BY sobre esta tabla preserva
    // el orden de insercion, igual que se espera de Postgres en produccion.
    @Test
    void debeRetornarLos3EstadosEnElOrdenDeInsercion_cuandoLaTablaEstaPoblada() {
        // Arrange
        sembrarEstado("PENDIENTE", "Pendiente", "Indica que una evaluación está pendiente por realizar");
        sembrarEstado("EN_PROGRESO", "En progreso", "Indica que una evaluación está en curso");
        sembrarEstado("FINALIZADA", "Finalizada", "Indica que una evaluación ha sido finalizada");
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EstadoEvaluacionReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado)
                .extracting(EstadoEvaluacionReadModel::id)
                .containsExactly("PENDIENTE", "EN_PROGRESO", "FINALIZADA");
    }

    @Test
    void debeRetornarListaVacia_cuandoLaTablaEstaVacia() {
        // Act & Assert
        assertThat(adapter.consultarTodos()).isEmpty();
    }

    // Criterio de aceptación #4 del plan: "el catálogo cambia en el futuro (nueva fila) -> el
    // endpoint la refleja sin cambios de código". El adapter no debe filtrar por los 3 ids
    // conocidos del enum de dominio (esta es una consulta de solo lectura sin lado command/): una
    // cuarta fila arbitraria debe aparecer igual que las 3 del catálogo actual.
    @Test
    void debeReflejarUnaFilaNueva_cuandoElCatalogoCrece() {
        // Arrange
        sembrarEstado("PENDIENTE", "Pendiente", "Indica que una evaluación está pendiente por realizar");
        sembrarEstado("EN_PROGRESO", "En progreso", "Indica que una evaluación está en curso");
        sembrarEstado("FINALIZADA", "Finalizada", "Indica que una evaluación ha sido finalizada");
        sembrarEstado("SUSPENDIDA", "Suspendida", "Estado hipotético agregado al catálogo en el futuro");
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EstadoEvaluacionReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado)
                .extracting(EstadoEvaluacionReadModel::id)
                .containsExactly("PENDIENTE", "EN_PROGRESO", "FINALIZADA", "SUSPENDIDA");
    }

    @Test
    void debeMapearCorrectamenteCadaCampo_delEntityAlReadModel() {
        // Arrange
        sembrarEstado("PENDIENTE", "Pendiente", "Indica que una evaluación está pendiente por realizar");
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EstadoEvaluacionReadModel> resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).singleElement().satisfies(estado -> {
            assertThat(estado.id()).isEqualTo("PENDIENTE");
            assertThat(estado.nombre()).isEqualTo("Pendiente");
            assertThat(estado.descripcion()).isEqualTo("Indica que una evaluación está pendiente por realizar");
        });
    }
}
