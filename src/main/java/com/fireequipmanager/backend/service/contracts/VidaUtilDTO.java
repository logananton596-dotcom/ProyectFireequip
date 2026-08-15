package com.fireequipmanager.backend.service.contracts;

import java.time.LocalDate;

public interface VidaUtilDTO {

    void setFechaVencimiento(LocalDate fecha);

    void setDiasRestantes(Long dias);

    void setProximoAVencer(Boolean valor);

    void setVidaUtilVencida(Boolean valor);

}