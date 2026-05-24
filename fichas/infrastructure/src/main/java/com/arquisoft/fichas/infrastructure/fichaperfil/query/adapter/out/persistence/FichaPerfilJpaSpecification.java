package com.arquisoft.fichas.infrastructure.fichaperfil.query.adapter.out.persistence;

import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilJpaEntity;
import com.arquisoft.shared.postgres.query.CampoSpec;
import com.arquisoft.shared.postgres.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Specification JPA concreta para FichaPerfilJpaEntity.
 *
 * Declara los campos filtrables y su tipo de dato. Toda la lógica de
 * recorrido del árbol y composición de predicados está en QueryJpaSpecification.
 *
 * OCP: agregar un campo filtrable solo requiere añadir una entrada al mapa.
 */
@Component
class FichaPerfilJpaSpecification extends QueryJpaSpecification<FichaPerfilJpaEntity> {

    private static final Map<String, CampoSpec<FichaPerfilJpaEntity>> CAMPOS = Map.of(
            "tituloProyecto", CampoSpec.texto(root -> root.get("tituloProyecto")),
            "asesorNombre",   CampoSpec.texto(root -> root.get("asesorFicha").get("nombre")),
            "asesorEmail",    CampoSpec.texto(root -> root.get("asesorFicha").get("email")),
            "asesorId",       CampoSpec.uuid(root -> root.get("asesorFicha").get("id"))
    );

    @Override
    protected Map<String, CampoSpec<FichaPerfilJpaEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
