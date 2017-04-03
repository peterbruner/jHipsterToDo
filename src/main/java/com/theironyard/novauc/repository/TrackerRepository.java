package com.theironyard.novauc.repository;

import com.theironyard.novauc.domain.Tracker;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Tracker entity.
 */
@SuppressWarnings("unused")
public interface TrackerRepository extends JpaRepository<Tracker,Long> {

}
