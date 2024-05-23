package com.juny.spacestory.repository;

import com.juny.spacestory.domain.Host;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostRepository extends JpaRepository<Host, Long> {
}
