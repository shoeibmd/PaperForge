package com.paperforge.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pipeline_steps")
public class PipelineStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "step_order", nullable = false)
    private int stepOrder;

    @Column(name = "tool_id", nullable = false, length = 50)
    private String toolId;

    @Column(name = "step_params_json", length = 2000)
    private String stepParamsJson;

    public PipelineStep() {}

    public PipelineStep(int stepOrder, String toolId, String stepParamsJson) {
        this.stepOrder = stepOrder;
        this.toolId = toolId;
        this.stepParamsJson = stepParamsJson;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getStepOrder() { return stepOrder; }
    public void setStepOrder(int stepOrder) { this.stepOrder = stepOrder; }

    public String getToolId() { return toolId; }
    public void setToolId(String toolId) { this.toolId = toolId; }

    public String getStepParamsJson() { return stepParamsJson; }
    public void setStepParamsJson(String stepParamsJson) { this.stepParamsJson = stepParamsJson; }
}
