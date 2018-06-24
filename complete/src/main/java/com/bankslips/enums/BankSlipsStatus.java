package com.bankslips.enums;

public enum BankSlipsStatus {
	
	PENDING("Pendente"),
	PAID("Pago"),
	CANCELED("Canceled");

	private String status;
	
	BankSlipsStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}

}
