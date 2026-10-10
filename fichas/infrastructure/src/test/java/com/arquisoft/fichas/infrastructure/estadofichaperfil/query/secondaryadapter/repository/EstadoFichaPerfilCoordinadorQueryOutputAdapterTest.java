package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoficha.command.secondaryadapter.entity.EstadoFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.entity.EstadoFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.entity.FichaPerfilJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoFichaPerfilCoordinadorQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstadoFichaPerfilEstudianteQueryRepository estudianteRepository;

    @Autowired
    private EstadoFichaPerfilAsesorQueryRepository asesorRepository;

    @Autowired
    private EstadoFichaPerfilRepresentanteQueryRepository representanteRepository;

    @Autowired
    private EstadoFichaPerfilCoordinadorQueryRepository coordinadorRepository;

    private EstadoFichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoFichaPerfilQueryOutputAdapter(
                estudianteRepository,
                asesorRepository,
                new EstadoFichaPerfilAsesorJpaSpecification(),
                representanteRepository,
                coordinadorRepository);

        persistirEstadoFicha("EN_CONSTRUCCION", "En Construccion");
        persistirEstadoFicha("DISPONIBLE_PARA_EVALUACION", "Disponible Para Evaluacion");
        persistirEstadoFicha("APROBADA", "Aprobada");
        persistirEstadoFicha("APROBADA_CON_OBSERVACIONES", "Aprobada Con Observaciones");
        persistirEstadoFicha("NO_APROBADA", "No Aprobada");
        persistirEstadoFicha("DESCARTADA", "Descartada");
    }

    @Test
    void debeDevolverHistorialCompleto_cuandoLaFichaTieneLosSeisEstados() {
        // Arrange
        var ficha = persistirFicha("Proyecto C1");
        var t0 = Instant.now().minus(6, ChronoUnit.HOURS);
        persistirTransicion(UtilUUID.generarNuevoUUID(), ficha, "DESCARTADA", t0.plus(5, ChronoUnit.HOURS));
        persistirTransicion(UtilUUID.generarNuevoUUID(), ficha, "EN_CONSTRUCCION", t0);
        persistirTransicion(UtilUUID.generarNuevoUUID(), ficha, "NO_APROBADA", t0.plus(4, ChronoUnit.HOURS));
        persistirTransicion(UtilUUID.generarNuevoUUID(), ficha, "DISPONIBLE_PARA_EVALUACION", t0.plus(1, ChronoUnit.HOURS));
        persistirTransicion(UtilUUID.generarNuevoUUID(), ficha, "APROBADA_CON_OBSERVACIONES", t0.plus(3, ChronoUnit.HOURS));
        persistirTransicion(UtilUUID.generarNuevoUUID(), ficha, "APROBADA", t0.plus(2, ChronoUnit.HOURS));
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorFicha(ficha);

        // Assert
        assertThat(resultado)
                .extracting(EstadoFichaPerfilReadModel::id)
                .containsExactly("EN_CONSTRUCCION", "DISPONIBLE_PARA_EVALUACION", "APROBADA",
                        "APROBADA_CON_OBSERVACIONES", "NO_APROBADA", "DESCARTADA");
        assertThat(resultado.get(0).nombre()).isEqualTo("En Construccion");
        assertThat(resultado.get(0).fechaActualizacion()).isNotNull();
    }

    @Test
    void debeOrdenarPorFechaAscLuegoId_cuandoHayEmpateDeFecha() {
        // Arrange
        var ficha = persistirFicha("Proyecto C2");
        var mismaFecha = Instant.now().minus(1, ChronoUnit.HOURS);
        var idMenor = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var idMayor = UUID.fromString("00000000-0000-0000-0000-000000000002");
        persistirTransicion(idMayor, ficha, "DISPONIBLE_PARA_EVALUACION", mismaFecha);
        persistirTransicion(idMenor, ficha, "EN_CONSTRUCCION", mismaFecha);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorFicha(ficha);

        // Assert
        assertThat(resultado)
                .extracting(EstadoFichaPerfilReadModel::id)
                .containsExactly("EN_CONSTRUCCION", "DISPONIBLE_PARA_EVALUACION");
    }

    @Test
    void debeNoDevolverEstadosDeOtraFicha_cuandoFiltraPorFicha() {
        // Arrange
        var fichaPedida = persistirFicha("Proyecto C3");
        var otraFicha = persistirFicha("Proyecto C4");
        persistirTransicion(UtilUUID.generarNuevoUUID(), fichaPedida, "EN_CONSTRUCCION", Instant.now());
        persistirTransicion(UtilUUID.generarNuevoUUID(), otraFicha, "APROBADA", Instant.now());
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorFicha(fichaPedida);

        // Assert
        assertThat(resultado)
                .extracting(EstadoFichaPerfilReadModel::id)
                .containsExactly("EN_CONSTRUCCION");
    }

    @Test
    void debeDevolverEstados_sinEstudianteNiEvaluacionAsociados() {
        // Arrange
        var ficha = persistirFicha("Proyecto C5");
        persistirTransicion(UtilUUID.generarNuevoUUID(), ficha, "EN_CONSTRUCCION", Instant.now());
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorFicha(ficha);

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    void debeDevolverListaVacia_cuandoLaFichaNoExiste() {
        // Act
        var resultado = adapter.consultarPorFicha(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    private void persistirEstadoFicha(String id, String nombre) {
        entityManager.persist(EstadoFichaJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion("Descripcion de " + nombre)
                .build());
    }

    private UUID persistirFicha(String titulo) {
        var asesor = AsesorFichaJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .identificador("DOC-" + titulo)
                .nombre("Asesor " + titulo)
                .email(titulo.replace(" ", "") + "@uco.edu.co")
                .ocurridoEn(Instant.now())
                .build();
        entityManager.persist(asesor);
        var ficha = FichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .tituloProyecto(titulo)
                .asesorFicha(asesor)
                .build();
        entityManager.persist(ficha);
        return ficha.getId();
    }

    private void persistirTransicion(UUID id, UUID fichaId, String estadoId, Instant fechaActualizacion) {
        var estadoRef = entityManager.getEntityManager()
                .getReference(EstadoFichaJpaEntity.class, estadoId);
        entityManager.persist(EstadoFichaPerfilJpaEntity.builder()
                .id(id)
                .fichaPerfilId(fichaId)
                .estadoFicha(estadoRef)
                .fechaActualizacion(fechaActualizacion)
                .build());
    }
}
