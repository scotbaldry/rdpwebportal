package com.rdpportal.service;

import com.rdpportal.dto.AssignmentDto;
import com.rdpportal.dto.ConnectionDto;
import com.rdpportal.dto.CreateMachineRequest;
import com.rdpportal.dto.MachineDto;
import com.rdpportal.model.Machine;
import com.rdpportal.model.User;
import com.rdpportal.model.UserMachineAssignment;
import com.rdpportal.repository.MachineRepository;
import com.rdpportal.repository.UserMachineAssignmentRepository;
import com.rdpportal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MachineService {

    private final MachineRepository machineRepository;
    private final UserRepository userRepository;
    private final UserMachineAssignmentRepository assignmentRepository;

    public MachineService(MachineRepository machineRepository,
                          UserRepository userRepository,
                          UserMachineAssignmentRepository assignmentRepository) {
        this.machineRepository = machineRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public List<MachineDto> getAllMachines() {
        return machineRepository.findAll().stream().map(MachineDto::new).toList();
    }

    public MachineDto getMachineById(Long id) {
        return machineRepository.findById(id).map(MachineDto::new)
            .orElseThrow(() -> new RuntimeException("Machine not found"));
    }

    public MachineDto createMachine(CreateMachineRequest request) {
        Machine machine = new Machine(request.getDisplayName(), request.getHostname());
        machine.setDescription(request.getDescription());
        machine.setIcon(request.getIcon());
        machine.setRdpPort(request.getRdpPort());
        machine.setEnabled(request.isEnabled());
        return new MachineDto(machineRepository.save(machine));
    }

    public MachineDto updateMachine(Long id, CreateMachineRequest request) {
        Machine machine = machineRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Machine not found"));
        machine.setDisplayName(request.getDisplayName());
        machine.setHostname(request.getHostname());
        machine.setDescription(request.getDescription());
        machine.setIcon(request.getIcon());
        machine.setRdpPort(request.getRdpPort());
        machine.setEnabled(request.isEnabled());
        return new MachineDto(machineRepository.save(machine));
    }

    @Transactional
    public void deleteMachine(Long id) {
        assignmentRepository.findByMachineId(id)
            .forEach(a -> assignmentRepository.delete(a));
        machineRepository.deleteById(id);
    }

    public List<MachineDto> getMachinesForUser(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return assignmentRepository.findByUserId(user.getId()).stream()
            .map(UserMachineAssignment::getMachine)
            .filter(Machine::isEnabled)
            .map(MachineDto::new)
            .toList();
    }

    public List<ConnectionDto> getConnectionsForUser(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return assignmentRepository.findByUserId(user.getId()).stream()
            .filter(a -> a.getMachine().isEnabled())
            .map(ConnectionDto::new)
            .toList();
    }

    public List<AssignmentDto> getAllAssignments() {
        return assignmentRepository.findAll().stream().map(AssignmentDto::new).toList();
    }

    public AssignmentDto createAssignment(Long userId, Long machineId, String rdpUsername, String rdpDomain, String rdpPassword, boolean useMultimon) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Machine machine = machineRepository.findById(machineId)
            .orElseThrow(() -> new RuntimeException("Machine not found"));
        if (assignmentRepository.findByUserIdAndMachineId(userId, machineId).isPresent()) {
            throw new RuntimeException("This machine is already assigned to the user");
        }
        UserMachineAssignment assignment = new UserMachineAssignment(user, machine);
        assignment.setRdpUsername(rdpUsername);
        assignment.setRdpDomain(rdpDomain);
        assignment.setRdpPassword(rdpPassword);
        assignment.setUseMultimon(useMultimon);
        return new AssignmentDto(assignmentRepository.save(assignment));
    }

    public AssignmentDto setAssignmentMultimon(Long id, boolean useMultimon) {
        UserMachineAssignment a = assignmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Assignment not found"));
        a.setUseMultimon(useMultimon);
        return new AssignmentDto(assignmentRepository.save(a));
    }

    @Transactional
    public void deleteAssignment(Long id) {
        assignmentRepository.deleteById(id);
    }
}
