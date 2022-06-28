package com.example.barbearia.api.controller;

import com.example.barbearia.api.dto.ServicoDTO;

import com.example.barbearia.exception.RegraNegocioException;
import com.example.barbearia.model.entity.Servico;
import com.example.barbearia.service.ServicoService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/servicos")
@RequiredArgsConstructor

public class ServicoController {
    private final ServicoService service;

    @GetMapping()
    @ApiOperation("Obter detalhes todos os servico")
    @ApiResponses({
            @ApiResponse(code=200, message="Servicos encontrados"),
            @ApiResponse(code=404, message="Servicos não encontrados")
    })
    public ResponseEntity get() {
        List<Servico> servicos = service.getServicos();
        return ResponseEntity.ok(servicos.stream().map(ServicoDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ApiOperation("Obter detalhes de um servico")
    @ApiResponses({
            @ApiResponse(code=200, message="Servico encontrado"),
            @ApiResponse(code=404, message="Servico não encontrado")
    })
    public ResponseEntity get(@PathVariable("id") @ApiParam("Id do servico") Long id) {
        Optional<Servico> servico = service.getServicoById(id);
        if (!servico.isPresent()) {
            return new ResponseEntity("Servico não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(servico.map(ServicoDTO::create));
    }

    @PostMapping()
    @ApiOperation("Salva um novo servico")
    @ApiResponses({
            @ApiResponse(code=200, message="Servico salvo com sucesso"),
            @ApiResponse(code=404, message="Erro ao salvar servico")
    })
    public ResponseEntity post(ServicoDTO dto) {
        try {
            Servico servico = converter(dto);
            servico = service.salvar(servico);
            return new ResponseEntity(servico, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    @ApiOperation("Atualiza um servico um servico")
    @ApiResponses({
            @ApiResponse(code=200, message="Servico salvo com sucesso"),
            @ApiResponse(code=404, message="Erro ao salvar servico")
    })
    public ResponseEntity atualizar(@PathVariable("id") Long id, ServicoDTO dto) {
        if (!service.getServicoById(id).isPresent()) {
            return new ResponseEntity("Servico não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Servico servico = converter(dto);
            servico.setId(id);
            service.salvar(servico);
            return ResponseEntity.ok(servico);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    @ApiOperation("Deleta um servico")
    @ApiResponses({
            @ApiResponse(code=200, message="Servico deletado com sucesso"),
            @ApiResponse(code=404, message="Erro ao deletar servico")
    })
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Servico> servico = service.getServicoById(id);
        if (!servico.isPresent()) {
            return new ResponseEntity("Servico não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(servico.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Servico converter(ServicoDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        Servico servico = modelMapper.map(dto, Servico.class);
        return servico;
    }

}

