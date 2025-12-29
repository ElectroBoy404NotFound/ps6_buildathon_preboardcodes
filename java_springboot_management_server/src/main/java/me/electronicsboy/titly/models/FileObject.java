package me.electronicsboy.titly.models;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Table(name = "files")
@Entity
public class FileObject {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "files_seq")
	@SequenceGenerator(name = "files_seq", sequenceName = "files_sequence", allocationSize = 1)
	@Basic(fetch = FetchType.EAGER)
	@Column(nullable = false)
    private long id;

	@Basic(fetch = FetchType.EAGER)
	@Column(nullable = false)
    private String filename;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "files_user_id", nullable = false)
    private User user;
	
	@Column(nullable = false)
    private String hash;
	
	@Column(nullable = true, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean transcribed = false;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
    
    public FileObject() {}
    
	public FileObject(String filename, User user, String hash) {
		this.filename = filename;
		this.user = user;
		this.hash = hash;
		this.transcribed = false;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public String getHash() {
		return hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}

	public boolean isTranscribed() {
		return transcribed;
	}

	public void setTranscribed(boolean transcribed) {
		this.transcribed = transcribed;
	}
}
