package me.electronicsboy.titly.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import me.electronicsboy.titly.models.Device;

@Repository
public interface DeviceRepository extends CrudRepository<Device, Long> {
	Optional<Device> findByDeviceId(String deviceId);
	List<Device> findAllByEnabled(boolean enabled);
}
