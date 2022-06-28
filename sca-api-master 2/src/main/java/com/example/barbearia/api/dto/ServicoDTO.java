package com.example.barbearia.api.dto;

import com.example.barbearia.model.entity.Servico;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ServicoDTO {

    private Long id;
    private String nome;
    private float preco;

    public static ServicoDTO create(Servico servico) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(servico, ServicoDTO.class);
    }
}
