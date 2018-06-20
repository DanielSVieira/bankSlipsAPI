package hello;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloControllerIT {

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/");
    }

    @Test
    public void getHello() throws Exception {
        ResponseEntity<String> response = template.getForEntity(base.toString() + "index",
                String.class);
        assertThat(response.getBody(), equalTo("Greetings from Spring Boot!"));
    }
    
    @Test
    public void errorWhenInBankSlipsCreation() {
        // when
        ResponseEntity<BankSlips> bankSlipsResponse = template.postForEntity(base.toString() + "rest/bankslips/",
                new BankSlips(), BankSlips.class);
 
        // then
        assertThat(bankSlipsResponse.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }
    
    @Test
    public void canCreateANewBankSlips() {
    	BankSlips bankSlips = new BankSlips();
    	bankSlips.setDueDate(new Date());
    	bankSlips.setCustomer("joao");
    	bankSlips.setTotalInCents(new BigDecimal(10));
        ResponseEntity<BankSlips> bankSlipsResponse = template.postForEntity(base.toString() + "rest/bankslips/",
        		bankSlips, BankSlips.class);


        
        
        // then
        assertThat(bankSlipsResponse.getStatusCode(), equalTo(HttpStatus.CREATED));
    }
}
