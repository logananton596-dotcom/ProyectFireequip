package com.fireequipmanager.backend.service.rules;

import com.fireequipmanager.backend.service.contracts.VidaUtilDTO;
import com.fireequipmanager.backend.service.contracts.VidaUtilEntity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class VidaUtilRule {

    private VidaUtilRule() {
    }

    // Completa todos los indicadores de vida útil
    public static void completarIndicadoresDeVidaUtil(
            VidaUtilDTO dto,
            VidaUtilEntity entity) {

        if (dto == null
                || entity == null
                || entity.getFechaIncorporacion() == null
                || entity.getVidaUtilAnios() == null) {
            return;
        }

        LocalDate vencimiento = calcularFechaVencimiento(
                entity.getFechaIncorporacion(),
                entity.getVidaUtilAnios());

        long dias = diasRestantes(vencimiento);

        dto.setFechaVencimiento(vencimiento);
        dto.setDiasRestantes(dias);
        dto.setProximoAVencer(proximoAVencer(dias));
        dto.setVidaUtilVencida(vencido(dias));
    }

    // Calcula la fecha de vencimiento
    public static LocalDate calcularFechaVencimiento(
            LocalDate ingreso,
            Integer vidaUtil) {

        return ingreso.plusYears(vidaUtil);
    }

    // Calcula días restantes
    public static long diasRestantes(LocalDate vencimiento) {

        return ChronoUnit.DAYS.between(
                LocalDate.now(),
                vencimiento);
    }

    // Indica si está próximo a vencer
    public static boolean proximoAVencer(long dias) {

        return dias >= 0 && dias <= 30;
    }

    // Indica si la vida útil expiró
    public static boolean vencido(long dias) {

        return dias < 0;
    }
}