package com.example.barbearia.api.controller;

import com.example.barbearia.api.dto.AgendaDTO;

import com.example.barbearia.exception.RegraNegocioException;
import com.example.barbearia.model.entity.Agenda;
import com.example.barbearia.model.entity.Cliente;
import com.example.barbearia.model.entity.Equipe;
import com.example.barbearia.model.entity.Servico;
import com.example.barbearia.service.AgendaService;
import com.example.barbearia.service.ClienteService;
import com.example.barbearia.service.EquipeService;
import com.example.barbearia.service.ServicoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/agendas")
@RequiredArgsConstructor
@Api("API de Agenda")
public class AgendaController {

    private final AgendaService service;
    private final ClienteService clienteService;
    private final EquipeService equipeService;
    private final ServicoService servicoService;


    @GetMapping()
    @ApiOperation ("Obter detalhes de todas as agendas")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Agendas encontradas"),
            @ApiResponse(code = 404, message = "Agendas não encontradas")
    })
    public ResponseEntity get() {
        List<Agenda> agendas = service.getAgenda();
        return ResponseEntity.ok(agendas.stream().map(AgendaDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ApiOperation ("Obter detalhes de uma agenda")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Agenda encontrada"),
            @ApiResponse(code = 404, message = "Agenda não encontrada")
    })
    public ResponseEntity get(@PathVariable("id") @ApiParam("Id da agenda" )Long id) {
        Optional<Agenda> agenda = service.getAgendaById(id);
        if (!agenda.isPresent()) {
            return new ResponseEntity("Agenda não encontrada", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(agenda.map(AgendaDTO::create));
    }

    @PostMapping()
    @ApiOperation("Salva uma nova agenda")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Agenda salva com sucesso"),
            @ApiResponse(code = 404, message = "Erro ao salvar a agenda")
    })
    public ResponseEntity post(AgendaDTO dto) {
        try {
            Agenda agenda = converter(dto);
            agenda = service.salvar(agenda);
            return new ResponseEntity(agenda, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    @ApiOperation("Atualiza uma Agenda")
    @ApiResponses({
            @ApiResponse(code=200, message= "Agenda atualizada com sucesso"),
            @ApiResponse(code=404, message= "Erro ao atualizar agenda")
    })

    public ResponseEntity atualizar(@PathVariable("id") Long id, AgendaDTO dto) {
        if (!service.getAgendaById(id).isPresent()) {
            return new ResponseEntity("Agenda não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            Agenda agenda = converter(dto);
            agenda.setId(id);
            service.salvar(agenda);
            return ResponseEntity.ok(agenda);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    @ApiOperation("Deletar uma agenda")
    @ApiResponses({
            @ApiResponse(code=200, message = "Agenda deletada com sucesso"),
            @ApiResponse(code=404, message = "Erro ao deletar agenda")
    })
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Agenda> agenda = service.getAgendaById(id);
        if (!agenda.isPresent()) {
            return new ResponseEntity("Agenda não encontrada", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(agenda.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Agenda converter(AgendaDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        Agenda agenda = modelMapper.map(dto, Agenda.class);
        if (dto.getIdCliente() != null) {
            Optional<Cliente> cliente = clienteService.getClienteById(dto.getIdCliente());
            if (!cliente.isPresent()) {
                agenda.setCliente(null);
            } else {
                agenda.setCliente(cliente.get());
            }
        }
        if (dto.getIdEquipe() != null) {
            Optional<Equipe> equipe = equipeService.getEquipeById(dto.getIdEquipe());
            if (!equipe.isPresent()) {
                agenda.setEquipe(null);
            } else {
                agenda.setEquipe(equipe.get());
            }
        }
        if (dto.getIdServico() != null) {
            Optional<Servico> servico = servicoService.getServicoById(dto.getIdServico());
            if (!servico.isPresent()) {
                agenda.setServico(null);
            } else {
                agenda.setServico(servico.get());
            }
        }
        return agenda;
    }
}
