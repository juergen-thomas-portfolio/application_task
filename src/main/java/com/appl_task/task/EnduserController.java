package com.appl_task.task;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
class EnduserController {

  private final EnduserRepository repository;

  EnduserController(EnduserRepository repository) {
    this.repository = repository;
  }


  // Aggregate root
  // tag::get-aggregate-root[]
  @GetMapping("/users")
  CollectionModel<EntityModel<Enduser>> all() {

  List<EntityModel<Enduser>> users = repository.findAll().stream()
      .map(enduser -> EntityModel.of(enduser,
          linkTo(methodOn(EnduserController.class).one(enduser.getId())).withSelfRel(),
          linkTo(methodOn(EnduserController.class).all()).withRel("users")))
          .collect(Collectors.toList());

  return CollectionModel.of(users, linkTo(methodOn(EnduserController.class).all()).withSelfRel());
}
  // end::get-aggregate-root[]

  @PostMapping("/users")
  Enduser newUser(@RequestBody Enduser newUser) {
    return repository.save(newUser);
  }

  // Single item
  
  @GetMapping("/users/{id}")
    EntityModel<Enduser> one(@PathVariable Long id) {

  Enduser enduser = repository.findById(id) //
      .orElseThrow(() -> new EnduserNotFoundException(id));

  return EntityModel.of(enduser, //
      linkTo(methodOn(EnduserController.class).one(id)).withSelfRel(),
      linkTo(methodOn(EnduserController.class).all()).withRel("enduser"));
  }

  @PutMapping("/users/{id}")
  Enduser replaceUser(@RequestBody Enduser newUser, @PathVariable Long id) {
    
    return repository.findById(id)
      .map(enduser -> {
        enduser.setName(newUser.getName());
        enduser.setRole(newUser.getRole());
        return repository.save(enduser);
      })
      .orElseGet(() -> {
        newUser.setId(id);
        return repository.save(newUser);
      });
  }

  @DeleteMapping("/users/{id}")
  void deleteUser(@PathVariable Long id) {
    repository.deleteById(id);
  }
}