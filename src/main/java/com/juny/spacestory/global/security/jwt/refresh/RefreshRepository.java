package com.juny.spacestory.global.security.jwt.refresh;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<Refresh, Long>, CustomRefreshRepository {

  @Transactional
  void deleteByRefresh(String refresh);
}
