package me.electronicsboy.titly.dtos.ws;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import me.electronicsboy.titly.data.DeviceSecondRequestType;
import me.electronicsboy.titly.data.DeviceStatus;

public class DeviceMessageDataDto {
	private DeviceStatus status;
	private DeviceSecondRequestType requestType;
	private String data;
	
	@JsonCreator
	public DeviceMessageDataDto(
			@JsonProperty("status") DeviceStatus status, 
			@JsonProperty("requestType") DeviceSecondRequestType requestType, 
			@JsonProperty("data") String data
	) {
		super();
		this.status = status;
		this.requestType = requestType;
		this.data = data;
	}

	/**
	 * @return the status
	 */
	public DeviceStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(DeviceStatus status) {
		this.status = status;
	}

	/**
	 * @return the requestType
	 */
	public DeviceSecondRequestType getRequestType() {
		return requestType;
	}

	/**
	 * @param requestType the requestType to set
	 */
	public void setRequestType(DeviceSecondRequestType requestType) {
		this.requestType = requestType;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
}
