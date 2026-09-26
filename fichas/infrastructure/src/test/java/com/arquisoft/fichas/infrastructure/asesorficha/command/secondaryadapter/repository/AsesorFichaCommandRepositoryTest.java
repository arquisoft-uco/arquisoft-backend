package com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AsesorFichaCommandRepositoryTest {

    @Autowired
    private AsesorFichaCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private AsesorFichaJpaEntity sembrar(Instant ocurridoEn, Instant eliminadoEn) {
        return entityManager.persistFlushFind(AsesorFichaJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("20161020123")
                .nombre("Ana Perez")
                .email("ana@uco.edu.co")
                .ocurridoEn(ocurridoEn)
                .eliminadoEn(eliminadoEn)
                .build());
    }

    @Test
    void debeExcluirLosEliminados_cuandoBuscaSoloVigentes() {
        // Arrange
        var instante = Instant.parse("2026-09-01T10:00:00Z");
        var vigente = sembrar(instante, null);
        var eliminado = sembrar(instante, instante);

        // Act
        var encontradoVigente = repository.findByIdAndEliminadoEnIsNull(vigente.getId());
        var encontradoEliminado = repository.findByIdAndEliminadoEnIsNull(eliminado.getId());

        // Assert
        assertThat(encontradoVigente).isPresent();
        assertThat(encontradoEliminado).isEmpty();
        assertThat(repository.findById(eliminado.getId())).isPresent();
    }

    @Test
    void debeMarcarEliminadoYActualizarOcurridoEn_cuandoSeEliminaLogicamente() {
        // Arrange
        var asesorFicha = sembrar(Instant.parse("2026-09-01T10:00:00Z"), null);
        var baja = Instant.parse("2026-09-24T10:00:00Z");

        // Act
        var filas = repository.eliminarLogica(asesorFicha.getId(), baja);

        // Assert
        var guardado = entityManager.find(AsesorFichaJpaEntity.class, asesorFicha.getId());
        assertThat(filas).isEqualTo(1);
        assertThat(guardado.getEliminadoEn()).isEqualTo(baja);
        assertThat(guardado.getOcurridoEn()).isEqualTo(baja);
    }

    @Test
    void debeLimpiarEliminadoYRefrescarDatos_cuandoSeReactiva() {
        // Arrange
        var baja = Instant.parse("2026-09-10T10:00:00Z");
        var asesorFicha = sembrar(baja, baja);
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");

        // Act
        var filas = repository.reactivar(asesorFicha.getId(), "20161020999", "Ana Gomez",
                "ana.gomez@uco.edu.co", ocurridoEn);

        // Assert
        var guardado = entityManager.find(AsesorFichaJpaEntity.class, asesorFicha.getId());
        assertThat(filas).isEqualTo(1);
        assertThat(guardado.getEliminadoEn()).isNull();
        assertThat(guardado.getIdentificador()).isEqualTo("20161020999");
        assertThat(guardado.getNombre()).isEqualTo("Ana Gomez");
        assertThat(guardado.getEmail()).isEqualTo("ana.gomez@uco.edu.co");
        assertThat(guardado.getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
