package com.paperforge.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "pipelines",
        indexes = {
                @Index(name = "idx_pipelines_user_id", columnList = "user_id"),
                @Index(name = "idx_pipelines_created_at", columnList = "created_at")
        }
)
public class Pipeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "pipeline_id")
    @OrderBy("stepOrder ASC")
    private List<PipelineStep> steps = new ArrayList<>();

    public Pipeline() {}

    public Pipeline(Long userId, String name, String description) {
        this.userId = userId;
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<PipelineStep> getSteps() { return steps; }
    public void setSteps(List<PipelineStep> steps) { this.steps = steps; }
}
