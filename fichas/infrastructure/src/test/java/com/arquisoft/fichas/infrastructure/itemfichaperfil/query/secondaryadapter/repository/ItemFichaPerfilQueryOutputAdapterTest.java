package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.entity.FichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.entity.ItemFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.tipoitem.command.secondaryadapter.entity.TipoItemJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemFichaPerfilQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemFichaPerfilQueryRepository repository;

    @Autowired
    private ItemFichaPerfilEstudianteQueryRepository estudianteRepository;

    private ItemFichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ItemFichaPerfilQueryOutputAdapter(repository, estudianteRepository);

        persistirTipoItem("OBJETIVO_GENERAL", "Objetivo General");
        persistirTipoItem("ANTECEDENTES", "Antecedentes");
    }

    @Test
    void debeDevolverItemsDeLaFicha_cuandoAsesorEsElDueno() {
        // Arrange
        UUID asesor = persistirAsesor("DOC-001", "Ana Ruiz", "ana.ruiz@uco.edu.co");
        UUID ficha = persistirFicha("Proyecto A", asesor);
        persistirItem(ficha, "OBJETIVO_GENERAL", "Contenido del objetivo general");
        entityManager.flush();

        // Act
        List<ItemFichaPerfilReadModel> resultado = adapter.consultarPorFichaYAsesor(ficha, asesor);

        // Assert
        assertThat(resultado).singleElement().satisfies(item -> {
            assertThat(item.fichaPerfilId()).isEqualTo(ficha);
            assertThat(item.tipoItem()).isEqualTo("OBJETIVO_GENERAL");
            assertThat(item.tipoItemNombre()).isEqualTo("Objetivo General");
            assertThat(item.contenido()).isEqualTo("Contenido del objetivo general");
            assertThat(item.id()).isNotNull();
        });
    }

    @Test
    void debeDevolverVacio_cuandoAsesorNoEsElDueno() {
        // Arrange
        UUID asesorDueno = persistirAsesor("DOC-010", "Ana Ruiz", "ana10@uco.edu.co");
        UUID otroAsesor = persistirAsesor("DOC-011", "Luis Paz", "luis11@uco.edu.co");
        UUID ficha = persistirFicha("Proyecto B", asesorDueno);
        persistirItem(ficha, "OBJETIVO_GENERAL", "Contenido");
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.consultarPorFichaYAsesor(ficha, otroAsesor)).isEmpty();
    }

    @Test
    void debeDevolverVacio_cuandoFichaNoExiste() {
        // Act & Assert
        assertThat(adapter.consultarPorFichaYAsesor(UUID.randomUUID(), UUID.randomUUID())).isEmpty();
    }

    @Test
    void debeDevolverVacio_cuandoFichaSinItems() {
        // Arrange
        UUID asesor = persistirAsesor("DOC-020", "Ana Ruiz", "ana20@uco.edu.co");
        UUID ficha = persistirFicha("Proyecto C", asesor);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.consultarPorFichaYAsesor(ficha, asesor)).isEmpty();
    }

    @Test
    void debeOrdenarPorTipoItemNombreAscendente() {
        // Arrange
        UUID asesor = persistirAsesor("DOC-030", "Ana Ruiz", "ana30@uco.edu.co");
        UUID ficha = persistirFicha("Proyecto D", asesor);
        persistirItem(ficha, "OBJETIVO_GENERAL", "Contenido objetivo");
        persistirItem(ficha, "ANTECEDENTES", "Contenido antecedentes");
        entityManager.flush();

        // Act
        List<ItemFichaPerfilReadModel> resultado = adapter.consultarPorFichaYAsesor(ficha, asesor);

        // Assert
        assertThat(resultado)
                .extracting(ItemFichaPerfilReadModel::tipoItemNombre)
                .containsExactly("Antecedentes", "Objetivo General");
    }

    @Test
    void debeDevolverItemsDeLaFicha_cuandoEstudianteEstaVinculado() {
        // Arrange
        UUID estudiante = persistirEstudiante("EST-001", "Juan Perez", "juan.perez@uco.edu.co");
        UUID asesor = persistirAsesor("DOC-100", "Ana Ruiz", "ana.ruiz100@uco.edu.co");
        UUID ficha = persistirFicha("Proyecto E", asesor);
        persistirEstudianteFichaPerfil(ficha, estudiante);
        persistirItem(ficha, "OBJETIVO_GENERAL", "Contenido del objetivo general");
        entityManager.flush();

        // Act
        List<ItemFichaPerfilReadModel> resultado = adapter.consultarPorFichaYEstudiante(ficha, estudiante);

        // Assert
        assertThat(resultado).singleElement().satisfies(item -> {
            assertThat(item.fichaPerfilId()).isEqualTo(ficha);
            assertThat(item.tipoItem()).isEqualTo("OBJETIVO_GENERAL");
            assertThat(item.tipoItemNombre()).isEqualTo("Objetivo General");
            assertThat(item.contenido()).isEqualTo("Contenido del objetivo general");
            assertThat(item.id()).isNotNull();
        });
    }

    @Test
    void debeDevolverVacio_cuandoEstudianteNoEstaVinculado() {
        // Arrange
        UUID estudianteVinculado = persistirEstudiante("EST-010", "Juan Perez", "juan10@uco.edu.co");
        UUID otroEstudiante = persistirEstudiante("EST-011", "Maria Diaz", "maria11@uco.edu.co");
        UUID asesor = persistirAsesor("DOC-110", "Ana Ruiz", "ana110@uco.edu.co");
        UUID ficha = persistirFicha("Proyecto F", asesor);
        persistirEstudianteFichaPerfil(ficha, estudianteVinculado);
        persistirItem(ficha, "OBJETIVO_GENERAL", "Contenido");
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.consultarPorFichaYEstudiante(ficha, otroEstudiante)).isEmpty();
    }

    @Test
    void debeDevolverVacio_cuandoFichaDeEstudianteNoExiste() {
        // Act & Assert
        assertThat(adapter.consultarPorFichaYEstudiante(UUID.randomUUID(), UUID.randomUUID())).isEmpty();
    }

    @Test
    void debeTraerSoloItemsDeLaFichaPedida_cuandoEstudianteVinculadoAVariasFichas() {
        // Arrange
        UUID estudiante = persistirEstudiante("EST-020", "Juan Perez", "juan20@uco.edu.co");
        UUID asesor = persistirAsesor("DOC-120", "Ana Ruiz", "ana120@uco.edu.co");
        UUID fichaPedida = persistirFicha("Proyecto G", asesor);
        UUID otraFicha = persistirFicha("Proyecto H", asesor);
        persistirEstudianteFichaPerfil(fichaPedida, estudiante);
        persistirEstudianteFichaPerfil(otraFicha, estudiante);
        persistirItem(fichaPedida, "OBJETIVO_GENERAL", "Contenido de la ficha pedida");
        persistirItem(otraFicha, "ANTECEDENTES", "Contenido de otra ficha");
        entityManager.flush();

        // Act
        List<ItemFichaPerfilReadModel> resultado = adapter.consultarPorFichaYEstudiante(fichaPedida, estudiante);

        // Assert
        assertThat(resultado).singleElement().satisfies(item -> {
            assertThat(item.fichaPerfilId()).isEqualTo(fichaPedida);
            assertThat(item.contenido()).isEqualTo("Contenido de la ficha pedida");
        });
    }

    private UUID persistirEstudiante(String identificador, String nombre, String email) {
        EstudianteJpaEntity estudiante = EstudianteJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador(identificador)
                .nombre(nombre)
                .email(email)
                .build();
        entityManager.persist(estudiante);
        return estudiante.getId();
    }

    private void persistirEstudianteFichaPerfil(UUID fichaId, UUID estudianteId) {
        entityManager.persist(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaId)
                .estudianteId(estudianteId)
                .build());
    }

    private void persistirTipoItem(String id, String nombre) {
        entityManager.persist(TipoItemJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion("Descripcion de " + nombre)
                .build());
    }

    private UUID persistirAsesor(String identificador, String nombre, String email) {
        AsesorFichaJpaEntity asesor = AsesorFichaJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador(identificador)
                .nombre(nombre)
                .email(email)
                .build();
        entityManager.persist(asesor);
        return asesor.getId();
    }

    private UUID persistirFicha(String titulo, UUID asesorId) {
        AsesorFichaJpaEntity asesorRef = entityManager.getEntityManager()
                .getReference(AsesorFichaJpaEntity.class, asesorId);
        FichaPerfilJpaEntity ficha = FichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto(titulo)
                .asesorFicha(asesorRef)
                .build();
        entityManager.persist(ficha);
        return ficha.getId();
    }

    private void persistirItem(UUID fichaId, String tipoItemId, String contenido) {
        TipoItemJpaEntity tipoRef = entityManager.getEntityManager()
                .getReference(TipoItemJpaEntity.class, tipoItemId);
        entityManager.persist(ItemFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaId)
                .tipoItem(tipoRef)
                .contenido(contenido)
                .build());
    }
}
