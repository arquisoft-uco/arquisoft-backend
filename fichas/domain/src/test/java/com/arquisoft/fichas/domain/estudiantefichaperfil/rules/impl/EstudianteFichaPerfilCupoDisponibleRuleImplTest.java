package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.CupoEstudiantesExcedidoException;
import com.arquisoft.shared.message.FichasMessages;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

class EstudianteFichaPerfilCupoDisponibleRuleImplTest {

    private static final UUID FICHA_PERFIL = UUID.randomUUID();

    @Test
    void debeLanzarCupoEstudiantesExcedido_cuandoExistentes2MasNuevos2() {
        // Arrange
        var regla = new EstudianteFichaPerfilCupoDisponibleRuleImpl(portConExistentes(2L));

        // Act
        Throwable ex = catchThrowable(() -> regla.validar(relaciones(2)));

        // Assert
        assertThat(ex)
                .isInstanceOf(CupoEstudiantesExcedidoException.class)
                .hasMessage(FichasMessages.EstudianteFichaPerfil.LIMITE_EXCEDIDO_MSG.formatted(
                        FichasMessages.FichaPerfil.ESTUDIANTES_MAX
                ));

        assertThat(((CupoEstudiantesExcedidoException) ex).getErrorCode())
                .isEqualTo(FichasMessages.EstudianteFichaPerfil.LIMITE_ESTUDIANTES_EXCEDIDO);
    }

    @Test
    void debePermitirLimiteExacto_cuandoExistentes0MasNuevos3() {
        // Arrange
        var regla = new EstudianteFichaPerfilCupoDisponibleRuleImpl(portConExistentes(0L));

        // Act & Assert
        assertThatCode(() -> regla.validar(relaciones(FichasMessages.FichaPerfil.ESTUDIANTES_MAX)))
                .doesNotThrowAnyException();
    }

    @Test
    void debePermitirAsignacion_cuandoQuedaCupo() {
        // Arrange
        var regla = new EstudianteFichaPerfilCupoDisponibleRuleImpl(portConExistentes(1L));

        // Act & Assert
        assertThatCode(() -> regla.validar(relaciones(2))).doesNotThrowAnyException();
    }

    @Test
    void debeConsultarElCupoDeLaFichaDeLasRelaciones_cuandoValida() {
        // Arrange
        var port = new EstudianteFichaPerfilOutputPortStub(0L);
        var regla = new EstudianteFichaPerfilCupoDisponibleRuleImpl(port);

        // Act
        regla.validar(relaciones(1));

        // Assert
        assertThat(port.fichaConsultada).isEqualTo(FICHA_PERFIL);
    }

    private static List<EstudianteFichaPerfilAggregate> relaciones(int cantidad) {
        return EstudianteFichaPerfilAggregate.crear(
                FICHA_PERFIL,
                IntStream.range(0, cantidad).mapToObj(i -> UUID.randomUUID()).toList());
    }

    private static EstudianteFichaPerfilOutputPort portConExistentes(long existentes) {
        return new EstudianteFichaPerfilOutputPortStub(existentes);
    }

    /** fichas:domain no tiene Mockito en el classpath de test: el doble se escribe a mano. */
    private static final class EstudianteFichaPerfilOutputPortStub implements EstudianteFichaPerfilOutputPort {

        private final long existentes;
        private UUID fichaConsultada;

        private EstudianteFichaPerfilOutputPortStub(long existentes) {
            this.existentes = existentes;
        }

        @Override
        public long contarPorFichaPerfilId(UUID fichaPerfilId) {
            this.fichaConsultada = fichaPerfilId;
            return existentes;
        }

        @Override
        public void guardar(EstudianteFichaPerfilAggregate relacion) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean existePorFichaYEstudiante(UUID fichaPerfilId, UUID estudianteId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void eliminar(UUID fichaPerfilId, UUID estudianteId) {
            throw new UnsupportedOperationException();
        }
    }
}
