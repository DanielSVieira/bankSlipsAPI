package hello;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class NovaClasse {
    
	@RequestMapping(value = "/novaClasse", method = RequestMethod.POST)
	public ResponseEntity<Car> get(@RequestBody Car carParam) {
	    Car car = new Car();
	    car.setColor("Blue");
	    car.setMiles(100);
	    car.setVIN("1234");

	    return new ResponseEntity<Car>(car, HttpStatus.OK);
	}
	
	@RequestMapping(value="/novaClasse/{itemid}", method = RequestMethod.GET)
	public @ResponseBody String getitem(@PathVariable("itemid") String itemid) {   

	    return itemid;
	}
	
}
