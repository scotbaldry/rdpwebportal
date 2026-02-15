package com.rdpportal.repository;

import com.rdpportal.model.UserMachineAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserMachineAssignmentRepository extends JpaRepository<UserMachineAssignment, Long> {
    List<UserMachineAssignment> findByUserId(Long userId);
    List<UserMachineAssignment> findByMachineId(Long machineId);
    Optional<UserMachineAssignment> findByUserIdAndMachineId(Long userId, Long machineId);
    void deleteByUserIdAndMachineId(Long userId, Long machineId);
}
