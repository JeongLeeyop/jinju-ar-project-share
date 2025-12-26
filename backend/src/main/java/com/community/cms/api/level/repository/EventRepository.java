package com.community.cms.api.level.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.cms.entity2.Event;

public interface EventRepository extends JpaRepository<Event, Integer>{
    List<Event> findAll();
}
