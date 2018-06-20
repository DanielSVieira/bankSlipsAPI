package hello;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.bankslips.contants.BankSlipsStatus;
import com.bankslips.contants.ErrorMessages;
import com.bankslips.contants.ErrorTypes;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

@Entity
public class BankSlips {
	
	@Id @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String id;
	private Date due_date;
    @NotNull
//    @DecimalMin()
	private BigDecimal total_in_cents;
    @NotNull
    @Size(min = 3, max = 255)
	private String customer;
	private String status = BankSlipsStatus.PENDING;
	private BigDecimal fine;
	
	public BankSlips() {
	}
	
	public HashMap<String, String> validate() {
		HashMap<String, String> errorMessages = new HashMap<String, String>();
		if(this.getTotalInCents() == null) {
			errorMessages.put(ErrorTypes.TOTAL_IN_CENTS, ErrorMessages.TOTAL_IN_CENTS_NOT_INFORMED);
		}
		return errorMessages;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDueDate() {
		return due_date;
	}

	public void setDueDate(Date due_date) {
		this.due_date = due_date;
	}

	public BigDecimal getTotalInCents() {
		return total_in_cents;
	}

	public void setTotalInCents(BigDecimal total_in_cents) {
		this.total_in_cents = total_in_cents;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getFine() {
		return fine;
	}

	public void setFine(BigDecimal fine) {
		this.fine = fine;
	}

}
