package com.northstar.crm.customer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CustomerRepositoryIT {

  @Autowired CustomerRepository repository;

  @Test
  void saveAndFindByPublicId() {
    CustomerEntity entity = new CustomerEntity();
    entity.setPublicId("CUS-1001");
    entity.setFullName("Amina Khan");
    entity.setEmail("amina@example.com");
    entity.setStatus("ACTIVE");
    repository.save(entity);

    var found = repository.findByPublicId("CUS-1001");

    assertThat(found).isPresent();
    assertThat(found.get().getFullName()).isEqualTo("Amina Khan");
  }
}