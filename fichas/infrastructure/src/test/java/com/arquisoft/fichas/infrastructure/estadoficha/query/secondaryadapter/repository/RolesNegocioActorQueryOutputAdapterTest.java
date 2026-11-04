package com.arquisoft.fichas.infrastructure.estadoficha.query.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.representantecomite.command.secondaryadapter.entity.RepresentanteComiteJpaEntity;
import com.arquisoft.fichas.infrastructure.security.FichasRoles;
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
class RolesNegocioActorQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ActorRolNegocioQueryRepository repository;

    private RolesNegocioActorQueryOutputAdapter adapter;

    @BeforeEach
    void preparar() {
        adapter = new RolesNegocioActorQueryOutputAdapter(repository);
    }

    private UUID sembrarAsesor(Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persistAndFlush(AsesorFichaJpaEntity.builder().id(id).identificador(id.toString().substring(0, 20))
                .nombre("Asesor").email("a@uco.edu.co").ocurridoEn(Instant.now()).eliminadoEn(eliminadoEn).build());
        return id;
    }

    private UUID sembrarRepresentante(UUID id, Instant eliminadoEn) {
        entityManager.persistAndFlush(RepresentanteComiteJpaEntity.builder().id(id).identificador(id.toString().substring(0, 20))
                .nombre("Representante").email("r@uco.edu.co").ocurridoEn(Instant.now()).eliminadoEn(eliminadoEn).build());
        return id;
    }

    @Test
    void debeRetornarAsesorFicha_cuandoVigente() {
        // Arrange
        var actor = sembrarAsesor(null);

        // Act
        var roles = adapter.consultarPorActor(actor);

        // Assert
        assertThat(roles).containsExactly(FichasRoles.Negocio.ASESOR_FICHA);
    }

    @Test
    void debeRetornarRepresentanteComite_cuandoVigente() {
        // Arrange
        var actor = sembrarRepresentante(UtilUUID.generarNuevoUUID(), null);

        // Act
        var roles = adapter.consultarPorActor(actor);

        // Assert
        assertThat(roles).containsExactly(FichasRoles.Negocio.REPRESENTANTE_COMITE);
    }

    @Test
    void debeRetornarAmbos_cuandoEsAsesorYRepresentante() {
        // Arrange
        var actor = sembrarAsesor(null);
        sembrarRepresentante(actor, null);

        // Act
        var roles = adapter.consultarPorActor(actor);

        // Assert
        assertThat(roles).containsExactlyInAnyOrder(
                FichasRoles.Negocio.ASESOR_FICHA, FichasRoles.Negocio.REPRESENTANTE_COMITE);
    }

    @Test
    void debeExcluirDadosDeBaja_cuandoElActorTieneEliminadoEn() {
        // Arrange
        var asesorDeBaja = sembrarAsesor(Instant.now());
        var representanteDeBaja = sembrarRepresentante(UtilUUID.generarNuevoUUID(), Instant.now());

        // Act / Assert
        assertThat(adapter.consultarPorActor(asesorDeBaja)).isEmpty();
        assertThat(adapter.consultarPorActor(representanteDeBaja)).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoNoExisteElActor() {
        // Act
        var roles = adapter.consultarPorActor(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(roles).isEmpty();
    }
}
