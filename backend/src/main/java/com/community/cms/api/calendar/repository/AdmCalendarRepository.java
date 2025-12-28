package com.community.cms.api.calendar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.Predicate;
import com.community.cms.entity2.Calendar;

public interface AdmCalendarRepository extends JpaRepository<Calendar, Integer>, QuerydslPredicateExecutor<Calendar> {
    List<Calendar> findAll(Predicate search);
}
