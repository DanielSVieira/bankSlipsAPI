package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BanckSlipsService {
	
	@Autowired
	private BankSlipsRepository bankSlipsRepository;
	
	public void create(BankSlips bankSlips) throws Exception{
		bankSlipsRepository.save(bankSlips);	
		
		
		System.out.println(bankSlipsRepository.findAll());
	}

}
