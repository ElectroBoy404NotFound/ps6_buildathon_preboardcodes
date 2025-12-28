package me.electronicsboy.titly.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.electronicsboy.titly.models.FileObject;

@Repository
public interface FileObjectRepository extends JpaRepository<FileObject, Long> {
	boolean existsByFilepath(String filepath);	
	boolean existsByFilename(String filename);
	boolean existsByGrade(String grade);
	boolean existsByTeacher(String teacher);
	boolean existsBySubject(String subject);
	boolean existsByTerm(String term);
	
	Optional<FileObject> findByFilepath(String filepath);
	Optional<List<FileObject>> findByFilename(String filename);
	Optional<List<FileObject>> findByGrade(String grade);
	Optional<List<FileObject>> findByTeacher(String teacher);
	Optional<List<FileObject>> findBySubject(String subject);
	Optional<List<FileObject>> findByTerm(String term);
}
