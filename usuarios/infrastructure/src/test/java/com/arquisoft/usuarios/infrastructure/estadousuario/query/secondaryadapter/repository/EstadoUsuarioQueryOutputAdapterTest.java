package com.arquisoft.usuarios.infrastructure.estadousuario.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.estadousuario.query.readmodel.EstadoUsuarioReadModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
@Sql(scripts = "/sql/estadousuario/esquema_estado_usuario.sql")
class EstadoUsuarioQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstadoUsuarioQueryRepository estadoUsuarioQueryRepository;

    private EstadoUsuarioQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoUsuarioQueryOutputAdapter(estadoUsuarioQueryRepository);
    }

    @Test
    void debeRetornarLosEstadosConTodasSusColumnas_cuandoExistenEnBD() {
        // Arrange
        persistirEstado("ACTIVO", "Activo", "Indica que un usuario puede desempeñarse");
        persistirEstado("INACTIVO", "Inactivo", "Indica que un usuario no puede desempeñarse");

        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado)
                .extracting(EstadoUsuarioReadModel::id, EstadoUsuarioReadModel::nombre, EstadoUsuarioReadModel::descripcion)
                .containsExactlyInAnyOrder(
                        tuple("ACTIVO", "Activo", "Indica que un usuario puede desempeñarse"),
                        tuple("INACTIVO", "Inactivo", "Indica que un usuario no puede desempeñarse"));
    }

    @Test
    void debeRetornarVacio_cuandoNoHayEstadosEnBD() {
        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).isEmpty();
    }

    private void persistirEstado(String id, String nombre, String descripcion) {
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO estado_usuario (id, nombre, descripcion) VALUES (?1, ?2, ?3)")
                .setParameter(1, id)
                .setParameter(2, nombre)
                .setParameter(3, descripcion)
                .executeUpdate();
    }
}
