package com.example.restservicespring;

class EmployeeNotFoundException extends RuntimeException {

    EmployeeNotFoundException(Long id) {
      super("Could not find employee " + id);
    }
  }
