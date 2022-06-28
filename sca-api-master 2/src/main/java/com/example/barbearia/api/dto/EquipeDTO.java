package com.example.barbearia.api.dto;

import com.example.barbearia.model.entity.Equipe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class EquipeDTO {

    private Long id;
    private String nome;
    private String especialidade;
    private int idade;
    private String descricao;


    public static EquipeDTO create(Equipe equipe) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(equipe, EquipeDTO.class);
    }
}
