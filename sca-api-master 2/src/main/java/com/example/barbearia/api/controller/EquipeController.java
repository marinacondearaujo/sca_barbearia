package com.example.barbearia.api.controller;

import com.example.barbearia.api.dto.EquipeDTO;

import com.example.barbearia.exception.RegraNegocioException;
import com.example.barbearia.model.entity.Equipe;
import com.example.barbearia.service.EquipeService;
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
@RequestMapping("/api/v1/equipes")
@RequiredArgsConstructor

public class EquipeController {

    private final EquipeService service;

    @GetMapping()
    @ApiOperation("Obter detalhes todas as equipes")
    @ApiResponses({
            @ApiResponse(code=200, message= "Equipes encontradas"),
            @ApiResponse(code=404, message="Equipes não encontradas")
    })
    public ResponseEntity get() {
        List<Equipe> equipes = service.getEquipes();
        return ResponseEntity.ok(equipes.stream().map(EquipeDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ApiOperation("Obter detalhes de uma equipe")
    @ApiResponses({
            @ApiResponse(code=200, message= "Equipe encontrada"),
            @ApiResponse(code=404, message="Equipe não encontrada")
    })
    public ResponseEntity get(@PathVariable("id") @ApiParam("Id da equipe") Long id) {
        Optional<Equipe> equipe = service.getEquipeById(id);
        if (!equipe.isPresent()) {
            return new ResponseEntity("Equipe não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(equipe.map(EquipeDTO::create));
    }

    @PostMapping()
    @ApiOperation("Salva uma nova equipe")
    @ApiResponses({
            @ApiResponse(code=200, message="Equipe salva com sucesso"),
            @ApiResponse(code = 404, message = "Erro ao salvar equipe")
    })
    public ResponseEntity post(EquipeDTO dto) {
        try {
            Equipe equipe = converter(dto);
            equipe = service.salvar(equipe);
            return new ResponseEntity(equipe, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    @ApiOperation("Atualiza uma equipe")
    @ApiResponses({
            @ApiResponse(code=200, message= "Equipe salva com sucesso"),
            @ApiResponse(code=404, message="Erro ao salvar equipe")
    })
    public ResponseEntity atualizar(@PathVariable("id") Long id, EquipeDTO dto) {
        if (!service.getEquipeById(id).isPresent()) {
            return new ResponseEntity("Equipe não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            Equipe equipe = converter(dto);
            equipe.setId(id);
            service.salvar(equipe);
            return ResponseEntity.ok(equipe);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    @ApiOperation("Deleta uma equipe")
    @ApiResponses({
            @ApiResponse(code=200, message="Equipe deletada com sucesso"),
            @ApiResponse(code=404, message = "Erro ao deletar equipe")
    })
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Equipe> equipe = service.getEquipeById(id);
        if (!equipe.isPresent()) {
            return new ResponseEntity("Equipe não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(equipe.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Equipe converter(EquipeDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        Equipe equipe = modelMapper.map(dto, Equipe.class);
        return equipe;
    }

}
