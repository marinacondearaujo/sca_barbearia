package com.example.barbearia.api.controller;

import com.example.barbearia.api.dto.ClienteDTO;

import com.example.barbearia.exception.RegraNegocioException;
import com.example.barbearia.model.entity.Cliente;
import com.example.barbearia.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import io.swagger.annotations.*;


@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Api("API de Cliente")



public class ClienteController {
    private final ClienteService service;

    @GetMapping()
    @ApiOperation ("Obter detalhes de todos os cliente")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Clientes encontrados"),
            @ApiResponse(code=404, message = "Clientes não encontrados")
    })
    public ResponseEntity get() {
        List<Cliente> clientes = service.getClientes();
        return ResponseEntity.ok(clientes.stream().map(ClienteDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ApiOperation ("Obter detalhes de um cliente")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cliente encontrado"),
            @ApiResponse(code=404, message = "Cliente não encontrado")
    })
    public ResponseEntity get(@PathVariable("id") @ApiParam ("Id do cliente")Long id) {
        Optional<Cliente> cliente = service.getClienteById(id);
        if (!cliente.isPresent()) {
            return new ResponseEntity("Cliente não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(cliente.map(ClienteDTO::create));
    }

    @PostMapping()
    @ApiOperation("Salva um novo cliente")
    @ApiResponses({
            @ApiResponse(code=201, message ="Cliente salvo com sucesso"),
            @ApiResponse(code=400, message = "Erro ao salvar cliente")
    })
    public ResponseEntity post(ClienteDTO dto) {
        try {
            Cliente cliente = converter(dto);
            cliente = service.salvar(cliente);
            return new ResponseEntity(cliente, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    @ApiOperation("Atualiza um cliente")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Cliente atualizado com sucesso"),
            @ApiResponse( code=404, message =  "Erro ao atualizar cliente")
    })
    public ResponseEntity atualizar(@PathVariable("id") Long id, ClienteDTO dto) {
        if (!service.getClienteById(id).isPresent()) {
            return new ResponseEntity("Cliente não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            Cliente cliente = converter(dto);
            cliente.setId(id);
            service.salvar(cliente);
            return ResponseEntity.ok(cliente);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    @ApiOperation("Deleta Cliente")
    @ApiResponses({
            @ApiResponse( code= 201, message="Cliente deletado com sucesso"),
            @ApiResponse(code=404, message ="Erro ao deletar cliente")
    })
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Cliente> cliente = service.getClienteById(id);
        if (!cliente.isPresent()) {
            return new ResponseEntity("Cliente não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(cliente.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Cliente converter(ClienteDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        Cliente cliente = modelMapper.map(dto, Cliente.class);
        return cliente;
    }

}
