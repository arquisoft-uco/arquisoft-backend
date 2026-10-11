package com.arquisoft.usuarios.domain.representantecomite.model;

import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;

import java.util.UUID;

public record DisponibilidadRepresentanteComiteUsuario(UUID usuario, RepresentanteComiteDomain representanteComite) {}
