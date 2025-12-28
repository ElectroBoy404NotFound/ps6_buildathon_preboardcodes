package me.electronicsboy.titly.models;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Table(name = "devices")
@Entity
public class Device {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "device_seq")
	@SequenceGenerator(name = "device_seq", sequenceName = "device_sequence", allocationSize = 1)
	@Column(nullable = false)
    private long id;

    @Column(nullable = true, unique = true)
    private String devicename;

    @Column(nullable = true, unique = true)
    private String deviceId;

    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private boolean enabled;
    
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
    
	public Device setId(long id) {
		this.id = id;
		return this;
	}

	public Device setDevicename(String devicename) {
		this.devicename = devicename;
		return this;
	}

	public Device setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		return this;
	}

	public Device setPassword(String password) {
		this.password = password;
		return this;
	}

	public Device setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public Device setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public long getId() {
		return id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}
	
    public Device setEnabled(boolean enabled) {
    	this.enabled = enabled;
    	return this;
    }

	/**
	 * @return the devicename
	 */
	public String getDevicename() {
		return devicename;
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}
}
