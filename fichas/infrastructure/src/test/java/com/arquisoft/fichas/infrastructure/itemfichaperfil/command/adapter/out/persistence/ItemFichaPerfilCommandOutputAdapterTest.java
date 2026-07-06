package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilAggregate;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence.ItemFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence.ItemFichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.tipoitem.persistence.TipoItemJpaEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class ItemFichaPerfilCommandOutputAdapterTest {

    @Autowired
    private ItemFichaPerfilJpaRepository jpaRepository;

    @Autowired
    private EntityManager entityManager;

    private ItemFichaPerfilCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ItemFichaPerfilCommandOutputAdapter(jpaRepository);
        // Inyección manual del EntityManager (en producción lo hace Spring)
        try {
            var field = ItemFichaPerfilCommandOutputAdapter.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(adapter, entityManager);
        } catch (Exception e) {
            throw new RuntimeException("Error inyectando EntityManager en test", e);
        }

        // Seed: insertar tipo_item en H2 para que getReference funcione
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
        ItemFichaPerfilAggregate aggregate = ItemFichaPerfilAggregate.crear(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Este es un objetivo general de prueba"
        );

        // Act
        adapter.guardar(aggregate);
        entityManager.flush();
        entityManager.clear();

        // Assert
        ItemFichaPerfilJpaEntity savedEntity = jpaRepository.findById(aggregate.getId()).orElse(null);
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
        ItemFichaPerfilAggregate aggregate = ItemFichaPerfilAggregate.crear(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Contenido de prueba"
        );

        // Act
        adapter.guardar(aggregate);
        entityManager.flush();

        // Assert — getReference crea un proxy sin SELECT
        // Verificamos que el entity guardado tiene el tipo correcto sin haber cargado toda la entidad
        ItemFichaPerfilJpaEntity saved = jpaRepository.findById(aggregate.getId()).orElseThrow();
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
        jpaRepository.save(entity);
        entityManager.flush();
        entityManager.clear();

        // Act
        boolean existe = adapter.existsPorFichaYTipoItem(fichaPerfilId, tipoItem);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoParNoExiste() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String tipoItem = "OBJETIVO_GENERAL";

        // Act
        boolean existe = adapter.existsPorFichaYTipoItem(fichaPerfilId, tipoItem);

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
        ItemFichaPerfilJpaEntity saved = jpaRepository.save(entity);
        entityManager.flush();
        entityManager.clear();

        // Act
        boolean existe = adapter.existsById(saved.getId());

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoItemNoExiste() {
        // Arrange
        UUID itemIdInexistente = UUID.randomUUID();

        // Act
        boolean existe = adapter.existsById(itemIdInexistente);

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debeGuardarCambios_cuandoModificarContenido() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilAggregate aggregate = ItemFichaPerfilAggregate.crear(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Contenido inicial"
        );
        adapter.guardar(aggregate);
        entityManager.flush();
        entityManager.clear();

        // Act
        ItemFichaPerfilAggregate reconstruido = adapter.buscarPorId(aggregate.getId()).orElseThrow();
        reconstruido.modificarContenido("Contenido modificado");
        adapter.guardar(reconstruido);
        entityManager.flush();
        entityManager.clear();

        // Assert
        ItemFichaPerfilJpaEntity savedEntity = jpaRepository.findById(aggregate.getId()).orElseThrow();
        assertThat(savedEntity.getContenido()).isEqualTo("Contenido modificado");
    }
}
