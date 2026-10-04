package com.arquisoft.fichas.infrastructure.estadoficha.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.fichas.infrastructure.estadoficha.command.secondaryadapter.entity.EstadoFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.security.FichasRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// estado_ficha_rol no tiene @Entity de comando (el backend no la escribe), asi que Hibernate no la
// genera con ddl-auto. Se crea aqui con el DDL de la migracion V20261001123448 para ejercitar el
// @Subselect real.
@DataJpaTest
class EstadoFichaQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstadoFichaQueryRepository repository;

    private EstadoFichaQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoFichaQueryOutputAdapter(repository);
        entityManager.getEntityManager().createNativeQuery("""
                CREATE TABLE IF NOT EXISTS estado_ficha_rol (
                    estado_ficha_id VARCHAR(50) NOT NULL,
                    rol             VARCHAR(60) NOT NULL,
                    PRIMARY KEY (estado_ficha_id, rol))
                """).executeUpdate();
        sembrarCatalogo();
    }

    @Test
    void debeRetornarLosEstadosDelAsesorOrdenadosPorId_cuandoElRolEsAsesorFicha() {
        // Act
        var resultado = adapter.consultarPorRoles(List.of(FichasRoles.Negocio.ASESOR_FICHA));

        // Assert
        assertThat(resultado)
                .extracting(EstadoFichaReadModel::id)
                .containsExactly("DESCARTADA", "DISPONIBLE_PARA_EVALUACION", "EN_CONSTRUCCION");
    }

    @Test
    void debeRetornarLosTerminalesSinDuplicados_cuandoVariosRolesHabilitanLosMismosEstados() {
        // Act
        var resultado = adapter.consultarPorRoles(
                List.of(FichasRoles.Negocio.COORDINADOR, FichasRoles.Negocio.REPRESENTANTE_COMITE));

        // Assert
        assertThat(resultado)
                .extracting(EstadoFichaReadModel::id)
                .containsExactly("APROBADA", "APROBADA_CON_OBSERVACIONES", "NO_APROBADA");
    }

    @Test
    void debeRetornarVacio_cuandoLaListaDeRolesEstaVacia() {
        // Act
        var resultado = adapter.consultarPorRoles(List.of());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeExcluirEstadosSinRolYRolesSinEstados_cuandoSeConsulta() {
        // Arrange
        persistirEstado("ESTADO_HUERFANO", "Huerfano", "Estado sin ningun rol habilitado");

        // Act
        var sinFilas = adapter.consultarPorRoles(List.of("ESTUDIANTE"));
        var todosLosRoles = adapter.consultarPorRoles(List.of(
                FichasRoles.Negocio.ASESOR_FICHA,
                FichasRoles.Negocio.COORDINADOR,
                FichasRoles.Negocio.REPRESENTANTE_COMITE));

        // Assert
        assertThat(sinFilas).isEmpty();
        assertThat(todosLosRoles)
                .hasSize(6)
                .extracting(EstadoFichaReadModel::id)
                .doesNotContain("ESTADO_HUERFANO");
    }

    @Test
    void debeProyectarTodasLasColumnas_cuandoLeeUnEstado() {
        // Act
        var resultado = adapter.consultarPorRoles(List.of(FichasRoles.Negocio.COORDINADOR));

        // Assert
        assertThat(resultado)
                .filteredOn(estado -> estado.id().equals("NO_APROBADA"))
                .singleElement()
                .satisfies(estado -> {
                    assertThat(estado.nombre()).isEqualTo("No Aprobada");
                    assertThat(estado.descripcion()).isEqualTo("Ficha rechazada");
                });
    }

    private void sembrarCatalogo() {
        persistirEstado("EN_CONSTRUCCION", "En Construccion", "Ficha en desarrollo");
        persistirEstado("DISPONIBLE_PARA_EVALUACION", "Disponible Para Evaluacion", "Lista para evaluar");
        persistirEstado("APROBADA", "Aprobada", "Ficha aprobada");
        persistirEstado("APROBADA_CON_OBSERVACIONES", "Aprobada Con Observaciones", "Aprobada con ajustes");
        persistirEstado("NO_APROBADA", "No Aprobada", "Ficha rechazada");
        persistirEstado("DESCARTADA", "Descartada", "Ficha descartada por el asesor");

        habilitar("EN_CONSTRUCCION", FichasRoles.Negocio.ASESOR_FICHA);
        habilitar("DISPONIBLE_PARA_EVALUACION", FichasRoles.Negocio.ASESOR_FICHA);
        habilitar("DESCARTADA", FichasRoles.Negocio.ASESOR_FICHA);
        for (var terminal : List.of("APROBADA", "APROBADA_CON_OBSERVACIONES", "NO_APROBADA")) {
            habilitar(terminal, FichasRoles.Negocio.COORDINADOR);
            habilitar(terminal, FichasRoles.Negocio.REPRESENTANTE_COMITE);
        }
    }

    private void persistirEstado(String id, String nombre, String descripcion) {
        entityManager.persist(EstadoFichaJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion(descripcion)
                .build());
        entityManager.flush();
    }

    private void habilitar(String estadoFicha, String rol) {
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO estado_ficha_rol (estado_ficha_id, rol) VALUES (?, ?)")
                .setParameter(1, estadoFicha)
                .setParameter(2, rol)
                .executeUpdate();
    }
}
