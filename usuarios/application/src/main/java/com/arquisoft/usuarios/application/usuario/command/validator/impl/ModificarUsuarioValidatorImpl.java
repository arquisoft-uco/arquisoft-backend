package com.arquisoft.usuarios.application.usuario.command.validator.impl;

import com.arquisoft.usuarios.application.usuario.command.validator.ModificarUsuarioValidator;
import com.arquisoft.usuarios.domain.usuario.ModificacionUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.DisponibilidadContactoUsuario;
import com.arquisoft.usuarios.domain.usuario.model.DisponibilidadEmailUsuario;
import com.arquisoft.usuarios.domain.usuario.model.DisponibilidadIdentificadorUsuario;
import com.arquisoft.usuarios.domain.usuario.model.EstadoActividadUsuario;
import com.arquisoft.usuarios.domain.usuario.model.ExistenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioActivoRule;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioContactoUnicoRule;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioEmailUnicoRule;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioExisteRule;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioIdentificadorUnicoRule;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioActivoRuleImpl;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioContactoUnicoRuleImpl;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioEmailUnicoRuleImpl;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioExisteRuleImpl;
import com.arquisoft.usuarios.domain.usuario.rules.impl.UsuarioIdentificadorUnicoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ModificarUsuarioValidatorImpl implements ModificarUsuarioValidator {

    private final UsuarioExisteRule usuarioExisteRule;
    private final UsuarioActivoRule usuarioActivoRule;
    private final UsuarioIdentificadorUnicoRule usuarioIdentificadorUnicoRule;
    private final UsuarioEmailUnicoRule usuarioEmailUnicoRule;
    private final UsuarioContactoUnicoRule usuarioContactoUnicoRule;

    public ModificarUsuarioValidatorImpl() {
        this.usuarioExisteRule = new UsuarioExisteRuleImpl();
        this.usuarioActivoRule = new UsuarioActivoRuleImpl();
        this.usuarioIdentificadorUnicoRule = new UsuarioIdentificadorUnicoRuleImpl();
        this.usuarioEmailUnicoRule = new UsuarioEmailUnicoRuleImpl();
        this.usuarioContactoUnicoRule = new UsuarioContactoUnicoRuleImpl();
    }

    @Override
    public void validar(ModificacionUsuarioDomain modificacion, UsuarioDomain encontrado,
                        boolean identificadorDuplicado, boolean emailDuplicado, boolean contactoDuplicado) {
        usuarioExisteRule.validar(new ExistenciaUsuario(modificacion.getUsuario(), encontrado));
        usuarioActivoRule.validar(new EstadoActividadUsuario(modificacion.getUsuario(), encontrado));
        usuarioIdentificadorUnicoRule.validar(
                new DisponibilidadIdentificadorUsuario(modificacion.getIdentificador(), identificadorDuplicado));
        usuarioEmailUnicoRule.validar(
                new DisponibilidadEmailUsuario(modificacion.getEmail(), emailDuplicado));
        usuarioContactoUnicoRule.validar(
                new DisponibilidadContactoUsuario(modificacion.getContacto(), contactoDuplicado));
    }
}
