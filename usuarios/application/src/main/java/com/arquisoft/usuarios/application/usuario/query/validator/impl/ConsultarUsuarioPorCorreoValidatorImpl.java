package com.arquisoft.usuarios.application.usuario.query.validator.impl;

import com.arquisoft.usuarios.application.usuario.query.validator.ConsultarUsuarioPorCorreoValidator;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.ExistenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.model.VigenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioExisteRule;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioNoEliminadoRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioExisteRuleImpl;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioNoEliminadoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ConsultarUsuarioPorCorreoValidatorImpl implements ConsultarUsuarioPorCorreoValidator {

    private final UsuarioExisteRule usuarioExisteRule;
    private final UsuarioNoEliminadoRule usuarioNoEliminadoRule;

    public ConsultarUsuarioPorCorreoValidatorImpl() {
        this.usuarioExisteRule = new UsuarioExisteRuleImpl();
        this.usuarioNoEliminadoRule = new UsuarioNoEliminadoRuleImpl();
    }

    @Override
    public void validar(UsuarioDomain encontrado) {
        usuarioExisteRule.validar(new ExistenciaUsuario(encontrado.getId(), encontrado));
        usuarioNoEliminadoRule.validar(new VigenciaUsuario(encontrado.getId(), encontrado));
    }
}
