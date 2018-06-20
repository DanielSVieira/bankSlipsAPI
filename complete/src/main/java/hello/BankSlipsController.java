package hello;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class BankSlipsController {
	
	@Autowired
	private BanckSlipsService bankSlipsService ;

	@RequestMapping(value = "/bankslips/", method = RequestMethod.POST)
    public ResponseEntity<BankSlips> create(@RequestBody BankSlips bankSlips) {
		
//		201 : Bankslip created
//		● 400 : Bankslip not provided in the request body
//		● 422 : Invalid bankslip provided.The possible reasons are:
//		○ A field of the provided bankslip was null or with invalid values
		try {
			bankSlipsService.create(bankSlips);	
		} catch (ConstraintViolationException e) {
			e.printStackTrace();
			return new ResponseEntity<BankSlips>(bankSlips, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			System.out.println("caiu no eception");
			e.printStackTrace();
			return new ResponseEntity<BankSlips>(bankSlips, HttpStatus.BAD_REQUEST);
		}
	 
		return new ResponseEntity<BankSlips>(bankSlips, HttpStatus.CREATED);
    }
	
	@RequestMapping(value = "/bankslips", method = RequestMethod.GET)
    public String list() {
		//retorna lista de bankslips
		return "ok";
    }
	
	@RequestMapping(value="/bankslips/{bankSlipsId}", method = RequestMethod.GET)
	public @ResponseBody String show(@PathVariable("bankSlipsId") String bankSlipsId) {   

		//detalhe de um bankslips
		//adicionar regras de juros (somento em cobranças em aberto - cobranças que ainda não foram pagas)
	    return bankSlipsId;
	}
	
	@RequestMapping(value="/bankslips/{bankSlipsId}", method = RequestMethod.PUT)
	public ResponseEntity<BankSlips> update(@RequestBody BankSlips bankSlips) {
		
		//adicionar regra pra cancelar um pagamento
		//adicionar regra pra pagar um boleto
		return new ResponseEntity<BankSlips>(bankSlips, HttpStatus.OK);
	}
	
	
	
	
}
