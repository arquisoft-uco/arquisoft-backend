package com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstudianteCommandRepositoryTest {

    @Autowired
    private EstudianteCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void debeRetornarTrue_cuandoEstudianteExiste() {
        // Arrange
        EstudianteJpaEntity estudiante = EstudianteJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("1234567890")
                .nombre("Estudiante de prueba")
                .email("estudiante@example.com")
                .ocurridoEn(Instant.now())
                .build();
        repository.save(estudiante);

        // Act
        boolean resultado = repository.existsById(estudiante.getId());

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoEstudianteNoExiste() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        boolean resultado = repository.existsById(id);

        // Assert
        assertThat(resultado).isFalse();
    }

    private EstudianteJpaEntity sembrar(Instant ocurridoEn, Instant eliminadoEn) {
        return entityManager.persistFlushFind(EstudianteJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("20161020123")
                .nombre("Ana Perez")
                .email("ana@uco.edu.co")
                .ocurridoEn(ocurridoEn)
                .eliminadoEn(eliminadoEn)
                .build());
    }

    @Test
    void debeDevolverSoloLosIdsVigentes_cuandoAlgunoEstaEliminado() {
        // Arrange
        var instante = Instant.parse("2026-09-01T10:00:00Z");
        var vigente1 = sembrar(instante, null);
        var eliminado = sembrar(instante, instante);
        var vigente2 = sembrar(instante, null);

        // Act
        var resultado = repository.findIdsVigentesByIdIn(
                List.of(vigente1.getId(), eliminado.getId(), vigente2.getId(), UUID.randomUUID()));

        // Assert
        assertThat(resultado).containsExactlyInAnyOrder(vigente1.getId(), vigente2.getId());
    }

    @Test
    void debeMarcarEliminadoYActualizarOcurridoEn_cuandoSeEliminaLogicamente() {
        // Arrange
        var estudiante = sembrar(Instant.parse("2026-09-01T10:00:00Z"), null);
        var baja = Instant.parse("2026-09-16T10:00:00Z");

        // Act
        var filas = repository.eliminarLogica(estudiante.getId(), baja);

        // Assert
        var guardado = entityManager.find(EstudianteJpaEntity.class, estudiante.getId());
        assertThat(filas).isEqualTo(1);
        assertThat(guardado.getEliminadoEn()).isEqualTo(baja);
        assertThat(guardado.getOcurridoEn()).isEqualTo(baja);
    }

    @Test
    void debeLimpiarEliminadoYRefrescarDatos_cuandoSeReactiva() {
        // Arrange
        var baja = Instant.parse("2026-09-10T10:00:00Z");
        var estudiante = sembrar(baja, baja);
        var ocurridoEn = Instant.parse("2026-09-16T10:00:00Z");

        // Act
        var filas = repository.reactivar(estudiante.getId(), "20161020999", "Ana Gomez",
                "ana.gomez@uco.edu.co", ocurridoEn);

        // Assert
        var guardado = entityManager.find(EstudianteJpaEntity.class, estudiante.getId());
        assertThat(filas).isEqualTo(1);
        assertThat(guardado.getEliminadoEn()).isNull();
        assertThat(guardado.getIdentificador()).isEqualTo("20161020999");
        assertThat(guardado.getNombre()).isEqualTo("Ana Gomez");
        assertThat(guardado.getEmail()).isEqualTo("ana.gomez@uco.edu.co");
        assertThat(guardado.getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
