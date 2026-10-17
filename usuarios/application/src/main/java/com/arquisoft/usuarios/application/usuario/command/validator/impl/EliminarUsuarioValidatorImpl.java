package com.arquisoft.usuarios.application.usuario.command.validator.impl;

import com.arquisoft.usuarios.application.usuario.command.validator.EliminarUsuarioValidator;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.ExistenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.model.RolesUsuario;
import com.arquisoft.usuarios.domain.usuario.model.VigenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioExisteRule;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioNoEliminadoRule;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioSinRolesVigentesRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioExisteRuleImpl;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioNoEliminadoRuleImpl;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioSinRolesVigentesRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EliminarUsuarioValidatorImpl implements EliminarUsuarioValidator {

    private final UsuarioExisteRule usuarioExisteRule;
    private final UsuarioNoEliminadoRule usuarioNoEliminadoRule;
    private final UsuarioSinRolesVigentesRule usuarioSinRolesVigentesRule;

    public EliminarUsuarioValidatorImpl() {
        this.usuarioExisteRule = new UsuarioExisteRuleImpl();
        this.usuarioNoEliminadoRule = new UsuarioNoEliminadoRuleImpl();
        this.usuarioSinRolesVigentesRule = new UsuarioSinRolesVigentesRuleImpl();
    }

    @Override
    public void validar(UUID usuario, UsuarioDomain encontrado, RolesUsuario roles) {
        usuarioExisteRule.validar(new ExistenciaUsuario(usuario, encontrado));
        usuarioNoEliminadoRule.validar(new VigenciaUsuario(usuario, encontrado));
        usuarioSinRolesVigentesRule.validar(roles);
    }
}
