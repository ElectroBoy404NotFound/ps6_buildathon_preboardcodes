package me.electronicsboy.titly.responses;

import me.electronicsboy.titly.data.FileType;
import me.electronicsboy.titly.models.FileObject;

public class SafeFileResponse {
	private long id;
    private String filename;
    private String grade;
    private String subject;
    private String term;
    private String teacher;
    private FileType fileType;
    
	public SafeFileResponse(long id, String filename, String grade, String subject, String term, String teacher, FileType fileType) {
		this.id = id;
		this.filename = filename;
		this.grade = grade;
		this.subject = subject;
		this.term = term;
		this.teacher = teacher;
		this.fileType = fileType;
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
	public FileType getFileType() {
		return fileType;
	}
	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public static SafeFileResponse fromFileObject(FileObject object) {
		return new SafeFileResponse(object.getId(), object.getFilename(), object.getGrade(), object.getSubject(), object.getTerm(), object.getTeacher(), object.getFiletype());
	}
}
