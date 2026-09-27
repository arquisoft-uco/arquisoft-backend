package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.usuario.query.criteria.UsuarioCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class UsuarioJpaSpecification extends QueryJpaSpecification<UsuarioJpaQueryEntity> {

    private static final Map<String, CampoSpec<UsuarioJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<UsuarioJpaQueryEntity>> m = new LinkedHashMap<>();
        for (UsuarioCriteria.Campo campo : UsuarioCriteria.Campo.values()) {
            CampoSpec<UsuarioJpaQueryEntity> spec = switch (campo) {
                case IDENTIFICADOR   -> CampoSpec.texto(root -> root.get("identificador"));
                case NOMBRE          -> CampoSpec.texto(root -> root.get("nombre"));
                case EMAIL           -> CampoSpec.texto(root -> root.get("email"));
                case CONTACTO        -> CampoSpec.texto(root -> root.get("contacto"));
                case ESTADO          -> CampoSpec.texto(root -> root.get("estado"));
                case VIGENTE         -> CampoSpec.booleano(root -> root.get("vigente"));
                case ES_ESTUDIANTE   -> CampoSpec.booleano(root -> root.get("esEstudiante"));
                case ES_ASESOR       -> CampoSpec.booleano(root -> root.get("esAsesor"));
                case ES_ASESOR_FICHA -> CampoSpec.booleano(root -> root.get("esAsesorFicha"));
                case ES_COORDINADOR  -> CampoSpec.booleano(root -> root.get("esCoordinador"));
                // TODO HU233: case ES_ADMINISTRADOR -> CampoSpec.booleano(root -> root.get("esAdministrador"));
                // TODO HU242: case ES_BIBLIOTECARIO -> CampoSpec.booleano(root -> root.get("esBibliotecario"));
                // TODO HU252: case ES_JURADO -> CampoSpec.booleano(root -> root.get("esJurado"));
                // TODO HU255: case ES_REPRESENTANTE_COMITE -> CampoSpec.booleano(root -> root.get("esRepresentanteComite"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<UsuarioJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
