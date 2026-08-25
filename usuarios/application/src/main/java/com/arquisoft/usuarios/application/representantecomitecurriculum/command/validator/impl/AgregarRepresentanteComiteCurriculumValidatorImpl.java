package com.arquisoft.usuarios.application.representantecomitecurriculum.command.validator.impl;

import com.arquisoft.usuarios.application.representantecomitecurriculum.command.validator.AgregarRepresentanteComiteCurriculumValidator;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.model.DisponibilidadRepresentante;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.model.ExistenciaUsuario;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.rules.RepresentanteComiteUnicoRule;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.rules.UsuarioExisteRule;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.rules.impl.RepresentanteComiteUnicoRuleImpl;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.rules.impl.UsuarioExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgregarRepresentanteComiteCurriculumValidatorImpl implements AgregarRepresentanteComiteCurriculumValidator {

    private final UsuarioExisteRule usuarioExisteRule;
    private final RepresentanteComiteUnicoRule representanteComiteUnicoRule;

    public AgregarRepresentanteComiteCurriculumValidatorImpl() {
        this.usuarioExisteRule = new UsuarioExisteRuleImpl();
        this.representanteComiteUnicoRule = new RepresentanteComiteUnicoRuleImpl();
    }

    @Override
    public void validar(UUID usuario, boolean usuarioExiste, boolean yaEsRepresentante) {
        usuarioExisteRule.validar(new ExistenciaUsuario(usuario, usuarioExiste));
        representanteComiteUnicoRule.validar(new DisponibilidadRepresentante(usuario, yaEsRepresentante));
    }
}
