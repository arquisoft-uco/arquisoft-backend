package com.arquisoft.evaluaciones.infrastructure.estadoevaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoEvaluacionQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EstadoEvaluacionQueryRepository repository;

    private EstadoEvaluacionQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoEvaluacionQueryOutputAdapter(repository);
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS estado_evaluacion (
                    id VARCHAR(60) NOT NULL,
                    nombre VARCHAR(60) NOT NULL,
                    descripcion VARCHAR(300) NOT NULL,
                    orden INTEGER,
                    PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery(
                "ALTER TABLE estado_evaluacion ADD COLUMN IF NOT EXISTS orden INTEGER").executeUpdate();
        entityManager.flush();
    }

    private void sembrarEstado(String id, String nombre, String descripcion, int orden) {
        entityManager.createNativeQuery(
                        "INSERT INTO estado_evaluacion (id, nombre, descripcion, orden) VALUES (?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, nombre)
                .setParameter(3, descripcion)
                .setParameter(4, orden)
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
    void debeRetornarLosEstadosOrdenadosPorOrden_cuandoSeInsertanDesordenados() {
        // Arrange
        sembrarEstado("FINALIZADA", "Finalizada", "Indica que una evaluación ha sido finalizada", 3);
        sembrarEstado("PENDIENTE", "Pendiente", "Indica que una evaluación está pendiente por realizar", 1);
        sembrarEstado("EN_PROGRESO", "En progreso", "Indica que una evaluación está en curso", 2);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodos();

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

    @Test
    void debeReflejarUnaFilaNueva_cuandoElCatalogoCrece() {
        // Arrange
        sembrarEstado("PENDIENTE", "Pendiente", "Indica que una evaluación está pendiente por realizar", 1);
        sembrarEstado("EN_PROGRESO", "En progreso", "Indica que una evaluación está en curso", 2);
        sembrarEstado("FINALIZADA", "Finalizada", "Indica que una evaluación ha sido finalizada", 3);
        sembrarEstado("SUSPENDIDA", "Suspendida", "Estado hipotético agregado al catálogo en el futuro", 4);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado)
                .extracting(EstadoEvaluacionReadModel::id)
                .containsExactly("PENDIENTE", "EN_PROGRESO", "FINALIZADA", "SUSPENDIDA");
    }

    @Test
    void debeMapearCorrectamenteCadaCampo_delEntityAlReadModel() {
        // Arrange
        sembrarEstado("PENDIENTE", "Pendiente", "Indica que una evaluación está pendiente por realizar", 1);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).singleElement().satisfies(estado -> {
            assertThat(estado.id()).isEqualTo("PENDIENTE");
            assertThat(estado.nombre()).isEqualTo("Pendiente");
            assertThat(estado.descripcion()).isEqualTo("Indica que una evaluación está pendiente por realizar");
        });
    }
}
