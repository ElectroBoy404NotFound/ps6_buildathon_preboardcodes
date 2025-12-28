package me.electronicsboy.titly.dtos.rest;

public class ResetPasswordDto {
	private String email;
	private String otp;
	private String newpassword;
	
	public ResetPasswordDto(String email, String otp, String newpassword) {
		super();
		this.email = email;
		this.otp = otp;
		this.newpassword = newpassword;
	}
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the otp
	 */
	public String getOtp() {
		return otp;
	}
	/**
	 * @param otp the otp to set
	 */
	public void setOtp(String otp) {
		this.otp = otp;
	}
	/**
	 * @return the newpassword
	 */
	public String getNewpassword() {
		return newpassword;
	}
	/**
	 * @param newpassword the newpassword to set
	 */
	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}
}
