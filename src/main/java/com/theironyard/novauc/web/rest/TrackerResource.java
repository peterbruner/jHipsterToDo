package com.theironyard.novauc.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.theironyard.novauc.domain.Tracker;

import com.theironyard.novauc.repository.TrackerRepository;
import com.theironyard.novauc.web.rest.util.HeaderUtil;
import com.theironyard.novauc.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Tracker.
 */
@RestController
@RequestMapping("/api")
public class TrackerResource {

    private final Logger log = LoggerFactory.getLogger(TrackerResource.class);

    private static final String ENTITY_NAME = "tracker";
        
    private final TrackerRepository trackerRepository;

    public TrackerResource(TrackerRepository trackerRepository) {
        this.trackerRepository = trackerRepository;
    }

    /**
     * POST  /trackers : Create a new tracker.
     *
     * @param tracker the tracker to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tracker, or with status 400 (Bad Request) if the tracker has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/trackers")
    @Timed
    public ResponseEntity<Tracker> createTracker(@Valid @RequestBody Tracker tracker) throws URISyntaxException {
        log.debug("REST request to save Tracker : {}", tracker);
        if (tracker.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new tracker cannot already have an ID")).body(null);
        }
        Tracker result = trackerRepository.save(tracker);
        return ResponseEntity.created(new URI("/api/trackers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /trackers : Updates an existing tracker.
     *
     * @param tracker the tracker to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tracker,
     * or with status 400 (Bad Request) if the tracker is not valid,
     * or with status 500 (Internal Server Error) if the tracker couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/trackers")
    @Timed
    public ResponseEntity<Tracker> updateTracker(@Valid @RequestBody Tracker tracker) throws URISyntaxException {
        log.debug("REST request to update Tracker : {}", tracker);
        if (tracker.getId() == null) {
            return createTracker(tracker);
        }
        Tracker result = trackerRepository.save(tracker);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, tracker.getId().toString()))
            .body(result);
    }

    /**
     * GET  /trackers : get all the trackers.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of trackers in body
     */
    @GetMapping("/trackers")
    @Timed
    public ResponseEntity<List<Tracker>> getAllTrackers(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Trackers");
        Page<Tracker> page = trackerRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/trackers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /trackers/:id : get the "id" tracker.
     *
     * @param id the id of the tracker to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tracker, or with status 404 (Not Found)
     */
    @GetMapping("/trackers/{id}")
    @Timed
    public ResponseEntity<Tracker> getTracker(@PathVariable Long id) {
        log.debug("REST request to get Tracker : {}", id);
        Tracker tracker = trackerRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(tracker));
    }

    /**
     * DELETE  /trackers/:id : delete the "id" tracker.
     *
     * @param id the id of the tracker to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/trackers/{id}")
    @Timed
    public ResponseEntity<Void> deleteTracker(@PathVariable Long id) {
        log.debug("REST request to delete Tracker : {}", id);
        trackerRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
