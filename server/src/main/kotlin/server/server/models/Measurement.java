package server.server.models;

import jakarta.persistence.*;

@Entity
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "measurement_id")
    private Long measurementId;

    private String name;

    public Measurement() {
    }

    public Measurement(String name) {
        this.name = name;
    }

    public Measurement(Long measurementId, String name) {
        this.measurementId = measurementId;
        this.name = name;
    }

    public Long getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(Long measurementId) {
        this.measurementId = measurementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "measurementId=" + measurementId +
                ", name='" + name + '\'' +
                '}';
    }
}
