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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import me.electronicsboy.titly.data.FileType;

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
	
	@Column(nullable = false)
    private FileType filetype;
    
    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = true)
    private String term;
    
    @Column(nullable = true)
    private String teacher;
    
    @Basic(fetch = FetchType.EAGER)
    @Column(nullable = false, unique = true)
    private String filepath;
    
    @Column(nullable = false)
    private boolean converted = false;
    
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
    
    public FileObject() {}
    
	public FileObject(String filename, FileType filetype, String grade, String subject, String term, String teacher, String filepath, boolean converted) {
		this.filename = filename;
		this.filetype = filetype;
		this.grade = grade;
		this.subject = subject;
		this.term = term;
		this.teacher = teacher;
		this.filepath = filepath;
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

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
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

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public boolean isConverted() {
		return converted;
	}

	public void setConverted(boolean converted) {
		this.converted = converted;
	}

	public FileType getFiletype() {
		return filetype;
	}

	public void setFiletype(FileType filetype) {
		this.filetype = filetype;
	}
}
