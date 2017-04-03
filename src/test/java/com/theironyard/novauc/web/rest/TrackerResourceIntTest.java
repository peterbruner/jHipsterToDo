package com.theironyard.novauc.web.rest;

import com.theironyard.novauc.JHipsterToDoApp;

import com.theironyard.novauc.domain.Tracker;
import com.theironyard.novauc.repository.TrackerRepository;
import com.theironyard.novauc.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TrackerResource REST controller.
 *
 * @see TrackerResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JHipsterToDoApp.class)
public class TrackerResourceIntTest {

    private static final String DEFAULT_ITEM = "AAAAAAAAAA";
    private static final String UPDATED_ITEM = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_COMPLETE = false;
    private static final Boolean UPDATED_IS_COMPLETE = true;

    @Autowired
    private TrackerRepository trackerRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTrackerMockMvc;

    private Tracker tracker;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TrackerResource trackerResource = new TrackerResource(trackerRepository);
        this.restTrackerMockMvc = MockMvcBuilders.standaloneSetup(trackerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tracker createEntity(EntityManager em) {
        Tracker tracker = new Tracker()
            .item(DEFAULT_ITEM)
            .location(DEFAULT_LOCATION)
            .isComplete(DEFAULT_IS_COMPLETE);
        return tracker;
    }

    @Before
    public void initTest() {
        tracker = createEntity(em);
    }

    @Test
    @Transactional
    public void createTracker() throws Exception {
        int databaseSizeBeforeCreate = trackerRepository.findAll().size();

        // Create the Tracker
        restTrackerMockMvc.perform(post("/api/trackers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracker)))
            .andExpect(status().isCreated());

        // Validate the Tracker in the database
        List<Tracker> trackerList = trackerRepository.findAll();
        assertThat(trackerList).hasSize(databaseSizeBeforeCreate + 1);
        Tracker testTracker = trackerList.get(trackerList.size() - 1);
        assertThat(testTracker.getItem()).isEqualTo(DEFAULT_ITEM);
        assertThat(testTracker.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testTracker.isIsComplete()).isEqualTo(DEFAULT_IS_COMPLETE);
    }

    @Test
    @Transactional
    public void createTrackerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = trackerRepository.findAll().size();

        // Create the Tracker with an existing ID
        tracker.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTrackerMockMvc.perform(post("/api/trackers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracker)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Tracker> trackerList = trackerRepository.findAll();
        assertThat(trackerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkIsCompleteIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackerRepository.findAll().size();
        // set the field null
        tracker.setIsComplete(null);

        // Create the Tracker, which fails.

        restTrackerMockMvc.perform(post("/api/trackers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracker)))
            .andExpect(status().isBadRequest());

        List<Tracker> trackerList = trackerRepository.findAll();
        assertThat(trackerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTrackers() throws Exception {
        // Initialize the database
        trackerRepository.saveAndFlush(tracker);

        // Get all the trackerList
        restTrackerMockMvc.perform(get("/api/trackers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tracker.getId().intValue())))
            .andExpect(jsonPath("$.[*].item").value(hasItem(DEFAULT_ITEM.toString())))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION.toString())))
            .andExpect(jsonPath("$.[*].isComplete").value(hasItem(DEFAULT_IS_COMPLETE.booleanValue())));
    }

    @Test
    @Transactional
    public void getTracker() throws Exception {
        // Initialize the database
        trackerRepository.saveAndFlush(tracker);

        // Get the tracker
        restTrackerMockMvc.perform(get("/api/trackers/{id}", tracker.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tracker.getId().intValue()))
            .andExpect(jsonPath("$.item").value(DEFAULT_ITEM.toString()))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION.toString()))
            .andExpect(jsonPath("$.isComplete").value(DEFAULT_IS_COMPLETE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTracker() throws Exception {
        // Get the tracker
        restTrackerMockMvc.perform(get("/api/trackers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTracker() throws Exception {
        // Initialize the database
        trackerRepository.saveAndFlush(tracker);
        int databaseSizeBeforeUpdate = trackerRepository.findAll().size();

        // Update the tracker
        Tracker updatedTracker = trackerRepository.findOne(tracker.getId());
        updatedTracker
            .item(UPDATED_ITEM)
            .location(UPDATED_LOCATION)
            .isComplete(UPDATED_IS_COMPLETE);

        restTrackerMockMvc.perform(put("/api/trackers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTracker)))
            .andExpect(status().isOk());

        // Validate the Tracker in the database
        List<Tracker> trackerList = trackerRepository.findAll();
        assertThat(trackerList).hasSize(databaseSizeBeforeUpdate);
        Tracker testTracker = trackerList.get(trackerList.size() - 1);
        assertThat(testTracker.getItem()).isEqualTo(UPDATED_ITEM);
        assertThat(testTracker.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testTracker.isIsComplete()).isEqualTo(UPDATED_IS_COMPLETE);
    }

    @Test
    @Transactional
    public void updateNonExistingTracker() throws Exception {
        int databaseSizeBeforeUpdate = trackerRepository.findAll().size();

        // Create the Tracker

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTrackerMockMvc.perform(put("/api/trackers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracker)))
            .andExpect(status().isCreated());

        // Validate the Tracker in the database
        List<Tracker> trackerList = trackerRepository.findAll();
        assertThat(trackerList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTracker() throws Exception {
        // Initialize the database
        trackerRepository.saveAndFlush(tracker);
        int databaseSizeBeforeDelete = trackerRepository.findAll().size();

        // Get the tracker
        restTrackerMockMvc.perform(delete("/api/trackers/{id}", tracker.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Tracker> trackerList = trackerRepository.findAll();
        assertThat(trackerList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tracker.class);
    }
}
