package com.appl_task.task;

class EnduserNotFoundException extends RuntimeException {

  EnduserNotFoundException(Long id) {
    super("Could not find enduser " + id);
  }
}
