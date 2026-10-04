package com.arquisoft.usuarios.infrastructure.bibliotecario.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.bibliotecario.query.criteria.BibliotecarioCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BibliotecarioSortMapperTest {

    @Test
    void debeTraducirCamposOrdenables_cuandoCampoValido() {
        // Act
        var identificador = BibliotecarioSortMapper.traducir("identificador");
        var nombre = BibliotecarioSortMapper.traducir("nombre");
        var email = BibliotecarioSortMapper.traducir("email");

        // Assert
        assertThat(identificador).isEqualTo("identificador");
        assertThat(nombre).isEqualTo("nombre");
        assertThat(email).isEqualTo("email");
    }

    @Test
    void debeRetornarNulo_cuandoCampoNoOrdenable() {
        // Act
        var estado = BibliotecarioSortMapper.traducir("estado");
        var vigente = BibliotecarioSortMapper.traducir("vigente");
        var contacto = BibliotecarioSortMapper.traducir("contacto");
        var inexistente = BibliotecarioSortMapper.traducir("campoInexistente");

        // Assert
        assertThat(estado).isNull();
        assertThat(vigente).isNull();
        assertThat(contacto).isNull();
        assertThat(inexistente).isNull();
    }

    @Test
    void debeResolverRuta_soloParaLosCamposQueElCriteriaDeclaraOrdenables() {
        for (var campo : BibliotecarioCriteria.Campo.values()) {
            // Act
            var ruta = BibliotecarioSortMapper.traducir(campo.getClave());

            // Assert
            assertThat(BibliotecarioCriteria.Campo.esValidoParaOrdenar(campo.getClave()))
                    .as("El campo '%s' es ordenable en el Criteria si y solo si el SortMapper le resuelve una ruta",
                            campo.getClave())
                    .isEqualTo(UtilObjeto.noEsNulo(ruta));
        }
    }
}
