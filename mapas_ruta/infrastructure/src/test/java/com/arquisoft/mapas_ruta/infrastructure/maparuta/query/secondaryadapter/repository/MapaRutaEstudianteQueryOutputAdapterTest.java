package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.entity.MapaRutaJpaEntity;
import com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(MapaRutaEstudianteQueryOutputAdapter.class)
class MapaRutaEstudianteQueryOutputAdapterTest {

    private static final LocalDate FECHA_INICIO = LocalDate.of(2026, 10, 1);
    private static final LocalDate FECHA_FIN = LocalDate.of(2026, 12, 1);

    @Autowired
    private MapaRutaEstudianteQueryOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    private UUID sembrarProyecto(String titulo) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder()
                .id(id)
                .estadoProyectoGradoId("EN_PROCESO")
                .coordinadorId(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(UtilUUID.generarNuevoUUID())
                .tituloProyecto(titulo)
                .build());
        return id;
    }

    private UUID sembrarMapa(UUID proyectoGrado, LocalDate inicio, LocalDate fin) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(MapaRutaJpaEntity.builder()
                .id(id)
                .proyectoGradoId(proyectoGrado)
                .fechaInicio(inicio)
                .fechaFin(fin)
                .build());
        return id;
    }

    @Test
    void debeRetornarElMapaConTituloYFechas_cuandoElProyectoTieneMapa() {
        // Arrange
        var proyectoGrado = sembrarProyecto("Sistema de gestion academica");
        var idMapa = sembrarMapa(proyectoGrado, FECHA_INICIO, FECHA_FIN);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorProyectoGrado(proyectoGrado);

        // Assert
        assertThat(resultado).hasValueSatisfying(readModel -> {
            assertThat(readModel.id()).isEqualTo(idMapa);
            assertThat(readModel.proyectoGrado()).isEqualTo(proyectoGrado);
            assertThat(readModel.tituloProyecto()).isEqualTo("Sistema de gestion academica");
            assertThat(readModel.fechaInicio()).isEqualTo(FECHA_INICIO);
            assertThat(readModel.fechaFin()).isEqualTo(FECHA_FIN);
        });
    }

    @Test
    void debeRetornarVacio_cuandoElProyectoNoTieneMapa() {
        // Arrange
        var proyectoSinMapa = sembrarProyecto("Proyecto sin mapa");
        var otroProyecto = sembrarProyecto("Proyecto con mapa");
        sembrarMapa(otroProyecto, FECHA_INICIO, FECHA_FIN);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorProyectoGrado(proyectoSinMapa);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarSoloElMapaDelProyectoPedido_cuandoOtroProyectoTambienTieneMapa() {
        // Arrange
        var proyectoPedido = sembrarProyecto("Proyecto pedido");
        var otroProyecto = sembrarProyecto("Proyecto ajeno");
        var idMapaPedido = sembrarMapa(proyectoPedido, FECHA_INICIO, FECHA_FIN);
        sembrarMapa(otroProyecto, LocalDate.of(2027, 1, 10), LocalDate.of(2027, 3, 10));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorProyectoGrado(proyectoPedido);

        // Assert
        assertThat(resultado).hasValueSatisfying(readModel -> {
            assertThat(readModel.id()).isEqualTo(idMapaPedido);
            assertThat(readModel.proyectoGrado()).isEqualTo(proyectoPedido);
            assertThat(readModel.tituloProyecto()).isEqualTo("Proyecto pedido");
            assertThat(readModel.fechaInicio()).isEqualTo(FECHA_INICIO);
        });
    }

    @Test
    void debeRetornarVacio_cuandoElMapaNoTieneReplicaDelProyecto() {
        // Arrange
        var proyectoSinReplica = UtilUUID.generarNuevoUUID();
        sembrarMapa(proyectoSinReplica, FECHA_INICIO, FECHA_FIN);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorProyectoGrado(proyectoSinReplica);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
