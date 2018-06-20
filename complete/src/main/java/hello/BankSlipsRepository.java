package hello;

import org.springframework.data.repository.CrudRepository;

public interface BankSlipsRepository extends CrudRepository<BankSlips, String> {

}
