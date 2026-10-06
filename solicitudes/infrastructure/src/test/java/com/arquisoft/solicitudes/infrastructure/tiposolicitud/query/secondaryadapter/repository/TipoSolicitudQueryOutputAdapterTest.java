package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.secondaryadapter.repository;

import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.solicitudes.infrastructure.tiposolicitud.command.secondaryadapter.entity.TipoSolicitudJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TipoSolicitudQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TipoSolicitudQueryRepository repository;

    private TipoSolicitudQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TipoSolicitudQueryOutputAdapter(repository);
    }

    @Test
    void debeRetornarSoloLosIdsPedidos_cuandoExistenMasTiposEnBD() {
        // Arrange
        persistirTipo("NOVEDAD_PARA_EL_COORDINADOR", "Novedad para el Coordinador",
                "Solicitud para temas que surgen de improvisto y que no están tipados");
        persistirTipo("CAMBIO_DE_ASESOR", "Cambio de Asesor", "Solicitud para modificar el asesor");
        persistirTipo("AMPLIACION_DE_PLAZO", "Ampliación de Plazo",
                "Solicitud para extender la fecha de entrega del proyecto");
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorIds(Set.of("NOVEDAD_PARA_EL_COORDINADOR", "CAMBIO_DE_ASESOR"));

        // Assert
        assertThat(resultado)
                .extracting(TipoSolicitudReadModel::id)
                .containsExactlyInAnyOrder("NOVEDAD_PARA_EL_COORDINADOR", "CAMBIO_DE_ASESOR");
    }

    @Test
    void debeIgnorarIdsInexistentes_cuandoSePidenIdsQueNoEstanEnBD() {
        // Arrange
        persistirTipo("CAMBIO_DE_ASESOR", "Cambio de Asesor", "Solicitud para modificar el asesor");
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorIds(Set.of("CAMBIO_DE_ASESOR", "NO_EXISTE"));

        // Assert
        assertThat(resultado).extracting(TipoSolicitudReadModel::id).containsExactly("CAMBIO_DE_ASESOR");
    }

    @Test
    void debeProyectarTodasLasColumnas_cuandoLeeUnTipo() {
        // Arrange
        persistirTipo("AMPLIACION_DE_PLAZO", "Ampliación de Plazo",
                "Solicitud para extender la fecha de entrega del proyecto");
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorIds(Set.of("AMPLIACION_DE_PLAZO"));

        // Assert
        assertThat(resultado).singleElement().satisfies(tipo -> {
            assertThat(tipo.id()).isEqualTo("AMPLIACION_DE_PLAZO");
            assertThat(tipo.nombre()).isEqualTo("Ampliación de Plazo");
            assertThat(tipo.descripcion()).isEqualTo("Solicitud para extender la fecha de entrega del proyecto");
        });
    }

    @Test
    void debeRetornarVacio_cuandoNingunIdCoincide() {
        // Act & Assert
        assertThat(adapter.consultarPorIds(Set.of("CAMBIO_DE_ASESOR"))).isEmpty();
    }

    private void persistirTipo(String id, String nombre, String descripcion) {
        entityManager.persist(TipoSolicitudJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion(descripcion)
                .build());
    }
}
