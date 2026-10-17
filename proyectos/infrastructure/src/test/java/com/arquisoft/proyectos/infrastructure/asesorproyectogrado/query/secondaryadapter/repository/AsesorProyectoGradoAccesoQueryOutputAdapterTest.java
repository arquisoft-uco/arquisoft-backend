package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
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
class AsesorProyectoGradoAccesoQueryOutputAdapterTest {

    private static final String ASIGNADO = "ASIGNADO";
    private static final String FINALIZADO = "FINALIZADO";

    @Autowired
    private AsesorProyectoGradoQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private AsesorProyectoGradoAccesoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AsesorProyectoGradoAccesoQueryOutputAdapter(repository);
        entityManager.persist(new EstadoAsesorFixtureJpaEntity(ASIGNADO, "Asignado", "Asesor asignado"));
        entityManager.persist(new EstadoAsesorFixtureJpaEntity(FINALIZADO, "Finalizado", "Asesor finalizado"));
    }

    private UUID asesor() {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new AsesorJpaEntity(id, "1001", "Ana", "1001@soyuco.edu.co",
                Instant.parse("2026-09-01T10:00:00Z"), null));
        return id;
    }

    private void vincular(UUID proyectoGrado, UUID asesor, String estado, Instant fechaAsignado) {
        entityManager.persist(new AsesorProyectoGradoJpaEntity(UtilUUID.generarNuevoUUID(), asesor, proyectoGrado,
                estado, fechaAsignado));
    }

    @Test
    void debeRetornarTrue_cuandoUltimaFilaAsignado() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = asesor();
        vincular(proyectoGrado, asesor, ASIGNADO, Instant.parse("2026-10-01T10:00:00Z"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.existeAsignacionVigente(asesor, proyectoGrado, ASIGNADO);

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoUltimaFilaFinalizada() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = asesor();
        vincular(proyectoGrado, asesor, ASIGNADO, Instant.parse("2026-09-10T10:00:00Z"));
        vincular(proyectoGrado, asesor, FINALIZADO, Instant.parse("2026-10-05T10:00:00Z"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.existeAsignacionVigente(asesor, proyectoGrado, ASIGNADO);

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    void debeRetornarFalse_cuandoOtroAsesorOOtroProyecto() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = asesor();
        vincular(proyectoGrado, asesor, ASIGNADO, Instant.parse("2026-10-01T10:00:00Z"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var otroAsesor = adapter.existeAsignacionVigente(UtilUUID.generarNuevoUUID(), proyectoGrado, ASIGNADO);
        var otroProyecto = adapter.existeAsignacionVigente(asesor, UtilUUID.generarNuevoUUID(), ASIGNADO);

        // Assert
        assertThat(otroAsesor).isFalse();
        assertThat(otroProyecto).isFalse();
    }
}
