package com.community.cms.api.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.community.cms.entity.Role;
import com.community.cms.entity.UserRole;
import com.community.cms.entity.UserRolePk;

public interface UserRoleRepository extends JpaRepository<UserRole, String>, QuerydslPredicateExecutor<UserRole> {

	void deleteByUserUid(String uid);

	void deleteByRole(Role role);
}
