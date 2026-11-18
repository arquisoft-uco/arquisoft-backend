package com.arquisoft.usuarios.application.usuario.query.validator.impl;

import com.arquisoft.usuarios.application.usuario.query.validator.ConsultarMiPerfilValidator;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.ExistenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.model.VigenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioExisteRule;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioNoEliminadoRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioExisteRuleImpl;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioNoEliminadoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsultarMiPerfilValidatorImpl implements ConsultarMiPerfilValidator {

    private final UsuarioExisteRule usuarioExisteRule;
    private final UsuarioNoEliminadoRule usuarioNoEliminadoRule;

    public ConsultarMiPerfilValidatorImpl() {
        this.usuarioExisteRule = new UsuarioExisteRuleImpl();
        this.usuarioNoEliminadoRule = new UsuarioNoEliminadoRuleImpl();
    }

    @Override
    public void validar(UUID usuario, UsuarioDomain encontrado) {
        usuarioExisteRule.validar(new ExistenciaUsuario(usuario, encontrado));
        usuarioNoEliminadoRule.validar(new VigenciaUsuario(usuario, encontrado));
    }
}
