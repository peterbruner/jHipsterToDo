package com.theironyard.novauc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Tracker.
 */
@Entity
@Table(name = "tracker")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Tracker implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "item")
    private String item;

    @Column(name = "location")
    private String location;

    @NotNull
    @Column(name = "is_complete", nullable = false)
    private Boolean isComplete;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public Tracker item(String item) {
        this.item = item;
        return this;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getLocation() {
        return location;
    }

    public Tracker location(String location) {
        this.location = location;
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean isIsComplete() {
        return isComplete;
    }

    public Tracker isComplete(Boolean isComplete) {
        this.isComplete = isComplete;
        return this;
    }

    public void setIsComplete(Boolean isComplete) {
        this.isComplete = isComplete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tracker tracker = (Tracker) o;
        if (tracker.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, tracker.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Tracker{" +
            "id=" + id +
            ", item='" + item + "'" +
            ", location='" + location + "'" +
            ", isComplete='" + isComplete + "'" +
            '}';
    }
}
