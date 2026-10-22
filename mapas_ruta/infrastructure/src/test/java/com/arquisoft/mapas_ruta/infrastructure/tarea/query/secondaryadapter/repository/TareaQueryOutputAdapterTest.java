package com.arquisoft.mapas_ruta.infrastructure.tarea.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.entity.MapaRutaJpaEntity;
import com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.entity.TareaJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TareaQueryOutputAdapter.class)
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
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(TareaJpaEntity.builder()
                .id(id)
                .mapaRutaId(mapa)
                .descripcion(descripcion)
                .asesorId(asesor)
                .estadoItemId("EN_PROGRESO")
                .build());
        return id;
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
