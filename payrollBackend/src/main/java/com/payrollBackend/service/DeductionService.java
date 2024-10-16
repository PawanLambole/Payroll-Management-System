package com.payrollBackend.service;

import com.payrollBackend.dto.DeductionDTO;
import com.payrollBackend.model.Deductions;
import com.payrollBackend.model.Employee;
import com.payrollBackend.repository.DeductionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DeductionService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DeductionRepository deductionRepository;

    public ResponseEntity<?> addDeduction(DeductionDTO deductionDTO){
        Employee employee = employeeService.findByEmployeeId(deductionDTO.getEmployeeId());
        if(employee == null){
            return new ResponseEntity<>("Employee not found", HttpStatus.BAD_REQUEST);
        }
        Deductions oldDeductions = deductionRepository.findByDeductionName(deductionDTO.getDeductionName());
        if (oldDeductions != null
                && oldDeductions.getDeductionName().equalsIgnoreCase(deductionDTO.getDeductionName())
                && Objects.equals(deductionDTO.getEmployeeId(), employee.getEmployeeId())) {

            return new ResponseEntity<>("Deduction already exists", HttpStatus.BAD_REQUEST);
        }

        Double deductionAmount = (deductionDTO.getDeductionPercentage()/100) * employee.getBaseSalary();
        Deductions newDeductions = new Deductions();
        newDeductions.setDeductionName(deductionDTO.getDeductionName());
        newDeductions.setDeductionType(deductionDTO.getDeductionType());
        newDeductions.setDeductionPercentage(deductionDTO.getDeductionPercentage());
        newDeductions.setDeductionAmount(deductionAmount);
        newDeductions.setEmployee(employee);
        deductionRepository.save(newDeductions);
        return new ResponseEntity<>("Deduction saved Successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<?> getDeductionByEmployeeId(Integer employeeId){
        List<Deductions> deductions = deductionRepository.findByEmployee_EmployeeId(employeeId);
        if(deductions.isEmpty()){
            return new ResponseEntity<>("Deductions not found", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(deductions, HttpStatus.OK);
    }

    public ResponseEntity<?> removeDeduction(Integer deductionId){
        Optional<Deductions> deduction = deductionRepository.findById(deductionId);
        if(deduction.isEmpty()){
            return new ResponseEntity<>("Deduction not found", HttpStatus.BAD_REQUEST);
        }
        deductionRepository.deleteById(deductionId);
        return new ResponseEntity<>("Deduction removed Successfully", HttpStatus.OK);
    }
}