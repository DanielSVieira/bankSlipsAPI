package hello;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BankSlipsTest {
	
    @Autowired
    private TestEntityManager entityManager;
	
    @Autowired
    private BankSlipsRepository bankSlipsRepository;

    
    @Before
    public void setUp() {

    }
   
//    @Test(expected = ConstraintViolationException.class)
//    public void invalidBankSlipTest() {
//    	BankSlips bankSlips = new BankSlips();
//
//        bankSlipsService.create(bankSlips);
//        entityManager.flush();
//     }
    
    @Test
    public void testFindByLastName() {
    	BankSlips bankSlips = new BankSlips();
        
        entityManager.persist(bankSlips);
//
//        List<Customer> findByLastName = customers.findByLastName(customer.getLastName());

//        assertThat(findByLastName).extracting(Customer::getLastName).containsOnly(customer.getLastName());
    }
	
}
