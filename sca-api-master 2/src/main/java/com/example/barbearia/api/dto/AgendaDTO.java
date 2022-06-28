package com.example.barbearia.api.dto;

import com.example.barbearia.model.entity.Agenda;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AgendaDTO {

    private Long id;
    private String data;
    private String horario;
    private String descricao;
    private Long idCliente;
    private Long idEquipe;
    private Long idServico;



    public static AgendaDTO create(Agenda agenda) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(agenda, AgendaDTO.class);
    }

}
