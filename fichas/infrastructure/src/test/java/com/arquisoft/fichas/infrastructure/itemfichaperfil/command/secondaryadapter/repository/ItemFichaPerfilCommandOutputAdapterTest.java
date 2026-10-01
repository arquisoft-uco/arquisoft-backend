package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.mapper.ItemFichaPerfilMapper;
import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.entity.ItemFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.tipoitem.command.secondaryadapter.entity.TipoItemJpaEntity;
import jakarta.persistence.EntityManager;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class ItemFichaPerfilCommandOutputAdapterTest {

    @Autowired
    private ItemFichaPerfilCommandRepository repository;

    @Autowired
    private EntityManager entityManager;

    private ItemFichaPerfilCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ItemFichaPerfilCommandOutputAdapter(repository, mock(AppLogger.class));

        // Seed: insertar tipo_item en H2 para que la referencia por id funcione
        entityManager.createNativeQuery(
                "INSERT INTO tipo_item (id, nombre, descripcion) VALUES (?, ?, ?)"
        )
                .setParameter(1, "OBJETIVO_GENERAL")
                .setParameter(2, "Objetivo General")
                .setParameter(3, "Descripción de prueba")
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void debeGuardar_cuandoItemEsValido() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Este es un objetivo general de prueba"
        );

        // Act
        adapter.registrarItem(ItemFichaPerfilMapper.toEntity(aggregate));
        entityManager.flush();
        entityManager.clear();

        // Assert
        ItemFichaPerfilJpaEntity savedEntity = repository.findById(aggregate.getId()).orElse(null);
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getId()).isEqualTo(aggregate.getId());
        assertThat(savedEntity.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(savedEntity.getTipoItem().getId()).isEqualTo("OBJETIVO_GENERAL");
        assertThat(savedEntity.getContenido()).isEqualTo("Este es un objetivo general de prueba");
    }

    @Test
    void debeUsarGetReference_cuandoGuarda() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Contenido de prueba"
        );

        // Act
        adapter.registrarItem(ItemFichaPerfilMapper.toEntity(aggregate));
        entityManager.flush();

        // Assert — getReference crea un proxy sin SELECT
        // Verificamos que el entity guardado tiene el tipo correcto sin haber cargado toda la entidad
        ItemFichaPerfilJpaEntity saved = repository.findById(aggregate.getId()).orElseThrow();
        assertThat(saved.getTipoItem()).isNotNull();
        assertThat(saved.getTipoItem().getId()).isEqualTo("OBJETIVO_GENERAL");
    }

    @Test
    void debeRetornarTrue_cuandoParExiste() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String tipoItem = "OBJETIVO_GENERAL";

        TipoItemJpaEntity tipoItemRef = entityManager.getReference(TipoItemJpaEntity.class, tipoItem);
        ItemFichaPerfilJpaEntity entity = ItemFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .tipoItem(tipoItemRef)
                .contenido("Contenido existente")
                .build();
        repository.save(entity);
        entityManager.flush();
        entityManager.clear();

        // Act
        boolean existe = adapter.existePorFichaYTipoItem(fichaPerfilId, tipoItem);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoParNoExiste() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String tipoItem = "OBJETIVO_GENERAL";

        // Act
        boolean existe = adapter.existePorFichaYTipoItem(fichaPerfilId, tipoItem);

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debeRetornarTrue_cuandoItemExiste() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        TipoItemJpaEntity tipoItemRef = entityManager.getReference(TipoItemJpaEntity.class, "OBJETIVO_GENERAL");
        ItemFichaPerfilJpaEntity entity = ItemFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .tipoItem(tipoItemRef)
                .contenido("Contenido existente")
                .build();
        ItemFichaPerfilJpaEntity saved = repository.save(entity);
        entityManager.flush();
        entityManager.clear();

        // Act
        boolean existe = adapter.existePorId(saved.getId());

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoItemNoExiste() {
        // Arrange
        UUID itemIdInexistente = UUID.randomUUID();

        // Act
        boolean existe = adapter.existePorId(itemIdInexistente);

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debeActualizarSoloElContenido_cuandoModificarContenido() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Contenido inicial"
        );
        adapter.registrarItem(ItemFichaPerfilMapper.toEntity(aggregate));
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.actualizarContenido(aggregate.getId(), "Contenido modificado");
        entityManager.flush();
        entityManager.clear();

        // Assert
        ItemFichaPerfilJpaEntity savedEntity = repository.findById(aggregate.getId()).orElseThrow();
        assertThat(savedEntity.getContenido()).isEqualTo("Contenido modificado");
        assertThat(savedEntity.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(savedEntity.getTipoItem().getId()).isEqualTo("OBJETIVO_GENERAL");
    }

    @Test
    void debeRetornarFichaPerfilId_cuandoItemExiste() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Contenido inicial"
        );
        adapter.registrarItem(ItemFichaPerfilMapper.toEntity(aggregate));
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<UUID> resultado = adapter.obtenerFichaPerfilId(aggregate.getId());

        // Assert
        assertThat(resultado).contains(fichaPerfilId);
    }

    @Test
    void debeRetornarVacio_cuandoItemNoExisteParaFichaPerfilId() {
        // Act
        Optional<UUID> resultado = adapter.obtenerFichaPerfilId(UUID.randomUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeEliminar_cuandoIdValido() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        TipoItemJpaEntity tipoItemRef = entityManager.getReference(TipoItemJpaEntity.class, "OBJETIVO_GENERAL");
        ItemFichaPerfilJpaEntity entity = ItemFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .tipoItem(tipoItemRef)
                .contenido("Contenido a eliminar")
                .build();
        ItemFichaPerfilJpaEntity saved = repository.save(entity);
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.removerItem(saved.getId());
        entityManager.flush();

        // Assert
        assertThat(repository.existsById(saved.getId())).isFalse();
    }

    @Test
    void debeTraerFichaPropiedadYUltimoEstado_cuandoElEstudianteEstaVinculado() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        var item = registrarItem(fichaPerfilId);
        insertarEstadoFicha("EN_CONSTRUCCION");
        insertarEstadoFicha("EN_REVISION");
        insertarEstadoFichaPerfil(UUID.randomUUID(), fichaPerfilId, "EN_CONSTRUCCION",
                Instant.parse("2026-01-01T00:00:00Z"));
        var ultimoEstado = UUID.randomUUID();
        insertarEstadoFichaPerfil(ultimoEstado, fichaPerfilId, "EN_REVISION",
                Instant.parse("2026-02-01T00:00:00Z"));
        insertarVinculo(fichaPerfilId, estudiante);

        // Act
        var resultado = adapter.obtenerPertenencia(item, estudiante);

        // Assert
        assertThat(resultado).hasValueSatisfying(pertenencia -> {
            assertThat(pertenencia.fichaPerfilId()).isEqualTo(fichaPerfilId);
            assertThat(pertenencia.esPropietario()).isTrue();
            assertThat(pertenencia.estadoId()).isEqualTo(ultimoEstado);
            assertThat(pertenencia.estadoFicha()).isEqualTo("EN_REVISION");
        });
    }

    @Test
    void debeMarcarNoPropietarioYSinEstado_cuandoNoHayVinculoNiEstado() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var item = registrarItem(fichaPerfilId);
        insertarVinculo(fichaPerfilId, UUID.randomUUID());

        // Act
        var resultado = adapter.obtenerPertenencia(item, UUID.randomUUID());

        // Assert
        assertThat(resultado).hasValueSatisfying(pertenencia -> {
            assertThat(pertenencia.fichaPerfilId()).isEqualTo(fichaPerfilId);
            assertThat(pertenencia.esPropietario()).isFalse();
            assertThat(pertenencia.estadoId()).isNull();
        });
    }

    @Test
    void debeTraerUnaSolaFila_cuandoDosEstadosEmpatanEnFecha() {
        // Arrange
        var fichaPerfilId = UUID.randomUUID();
        var item = registrarItem(fichaPerfilId);
        insertarEstadoFicha("EN_CONSTRUCCION");
        var fecha = Instant.parse("2026-03-01T00:00:00Z");
        var menor = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var mayor = UUID.fromString("00000000-0000-0000-0000-000000000002");
        insertarEstadoFichaPerfil(menor, fichaPerfilId, "EN_CONSTRUCCION", fecha);
        insertarEstadoFichaPerfil(mayor, fichaPerfilId, "EN_CONSTRUCCION", fecha);

        // Act
        var resultado = adapter.obtenerPertenencia(item, UUID.randomUUID());

        // Assert
        assertThat(resultado).hasValueSatisfying(pertenencia ->
                assertThat(pertenencia.estadoId()).isEqualTo(mayor));
    }

    @Test
    void debeRetornarVacio_cuandoElItemNoExisteParaPertenencia() {
        // Act
        var resultado = adapter.obtenerPertenencia(UUID.randomUUID(), UUID.randomUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    private UUID registrarItem(UUID fichaPerfilId) {
        var aggregate = ItemFichaPerfilDomain.crear(fichaPerfilId, "OBJETIVO_GENERAL", "Contenido");
        adapter.registrarItem(ItemFichaPerfilMapper.toEntity(aggregate));
        entityManager.flush();
        entityManager.clear();
        return aggregate.getId();
    }

    private void insertarEstadoFicha(String id) {
        entityManager.createNativeQuery("INSERT INTO estado_ficha (id, nombre, descripcion) VALUES (?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, id)
                .setParameter(3, id)
                .executeUpdate();
    }

    private void insertarEstadoFichaPerfil(UUID id, UUID fichaPerfilId, String estadoFicha, Instant fecha) {
        entityManager.createNativeQuery("INSERT INTO estado_ficha_perfil "
                        + "(id, ficha_perfil_id, estado_ficha_id, fecha_actualizacion) VALUES (?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, fichaPerfilId)
                .setParameter(3, estadoFicha)
                .setParameter(4, fecha)
                .executeUpdate();
    }

    private void insertarVinculo(UUID fichaPerfilId, UUID estudiante) {
        entityManager.createNativeQuery("INSERT INTO estudiante_ficha_perfil "
                        + "(id, ficha_perfil_id, estudiante_id) VALUES (?, ?, ?)")
                .setParameter(1, UUID.randomUUID())
                .setParameter(2, fichaPerfilId)
                .setParameter(3, estudiante)
                .executeUpdate();
    }
}
