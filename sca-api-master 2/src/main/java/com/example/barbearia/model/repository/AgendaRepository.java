package com.example.barbearia.model.repository;

import com.example.barbearia.model.entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
}
