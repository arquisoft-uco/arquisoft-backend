package com.arquisoft.usuarios.domain.asesor.model;

import com.arquisoft.usuarios.domain.asesor.AsesorDomain;

import java.util.UUID;

public record DisponibilidadAsesorUsuario(UUID usuario, AsesorDomain asesor) {}
