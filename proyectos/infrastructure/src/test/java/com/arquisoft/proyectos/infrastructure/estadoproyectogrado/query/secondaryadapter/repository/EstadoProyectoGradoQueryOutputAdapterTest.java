package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.estadoproyectogrado.command.secondaryadapter.entity.EstadoProyectoGradoJpaEntity;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository.EstadoProyectoFixtureJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoProyectoGradoQueryOutputAdapterTest {

    private static final String EN_PROCESO = "EN_PROCESO";
    private static final String ATRASADO = "ATRASADO";

    @Autowired
    private EstadoProyectoGradoQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private EstadoProyectoGradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoProyectoGradoQueryOutputAdapter(repository);
        entityManager.persist(new EstadoProyectoFixtureJpaEntity(EN_PROCESO, "En proceso", "En desarrollo"));
        entityManager.persist(new EstadoProyectoFixtureJpaEntity(ATRASADO, "Atrasado", "Con retraso"));
    }

    private UUID registrar(UUID id, UUID proyectoGrado, String estado, String fecha) {
        entityManager.persist(new EstadoProyectoGradoJpaEntity(id, proyectoGrado, estado, Instant.parse(fecha)));
        return id;
    }

    private UUID registrar(UUID proyectoGrado, String estado, String fecha) {
        return registrar(UtilUUID.generarNuevoUUID(), proyectoGrado, estado, fecha);
    }

    @Test
    void debeOrdenarPorFechaDescendenteYProyectarNombre_cuandoHayVariosEstados() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var antiguo = registrar(proyectoGrado, EN_PROCESO, "2026-09-10T10:00:00Z");
        var reciente = registrar(proyectoGrado, ATRASADO, "2026-10-05T10:00:00Z");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorProyecto(proyectoGrado);

        // Assert
        assertThat(resultado).extracting(r -> r.id()).containsExactly(reciente, antiguo);
        var primero = resultado.getFirst();
        assertThat(primero.estadoProyecto()).isEqualTo(ATRASADO);
        assertThat(primero.estadoProyectoNombre()).isEqualTo("Atrasado");
        assertThat(primero.fechaActualizacion()).isEqualTo(Instant.parse("2026-10-05T10:00:00Z"));
        var ultimo = resultado.getLast();
        assertThat(ultimo.estadoProyecto()).isEqualTo(EN_PROCESO);
        assertThat(ultimo.estadoProyectoNombre()).isEqualTo("En proceso");
    }

    @Test
    void debeDesempatarPorIdDescendente_cuandoFechasIguales() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var fecha = "2026-10-05T10:00:00Z";
        var uno = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var dos = UUID.fromString("00000000-0000-0000-0000-000000000002");
        var tres = UUID.fromString("00000000-0000-0000-0000-000000000003");
        registrar(uno, proyectoGrado, EN_PROCESO, fecha);
        registrar(tres, proyectoGrado, ATRASADO, fecha);
        registrar(dos, proyectoGrado, EN_PROCESO, fecha);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorProyecto(proyectoGrado);

        // Assert
        assertThat(resultado).extracting(r -> r.id()).containsExactly(tres, dos, uno);
    }

    @Test
    void debeAislarPorProyecto_cuandoHayEstadosDeOtroProyecto() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var propio = registrar(proyectoGrado, EN_PROCESO, "2026-10-05T10:00:00Z");
        registrar(UtilUUID.generarNuevoUUID(), ATRASADO, "2026-10-06T10:00:00Z");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorProyecto(proyectoGrado);

        // Assert
        assertThat(resultado).extracting(r -> r.id()).containsExactly(propio);
    }

    @Test
    void debeDevolverVacio_cuandoProyectoSinHistorial() {
        // Act
        var resultado = adapter.consultarPorProyecto(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
