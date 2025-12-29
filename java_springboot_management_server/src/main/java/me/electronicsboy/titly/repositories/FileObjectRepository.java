package me.electronicsboy.titly.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.electronicsboy.titly.models.FileObject;
import me.electronicsboy.titly.models.User;

@Repository
public interface FileObjectRepository extends JpaRepository<FileObject, Long> {
	boolean existsByFilename(String filename);
	boolean existsByUser(User user);
	
	Optional<List<FileObject>> findByFilename(String filename);
	Optional<List<FileObject>> findByUser(User user);
	Optional<List<FileObject>> findByHash(String hash);
}
