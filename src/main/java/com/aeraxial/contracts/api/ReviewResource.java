package com.aeraxial.contracts.api;

import com.aeraxial.contracts.domain.*;
import com.aeraxial.contracts.dto.*;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * CRUD para review_task y review_action.
 *
 *  POST/GET       /review-tasks
 *  GET/PUT/DELETE /review-tasks/{id}
 *  PATCH          /review-tasks/{id}/status
 *
 *  POST/GET       /review-actions
 *  GET/DELETE     /review-actions/{id}
 *
 * Al crear una ReviewAction de tipo APPROVE o REJECT,
 * actualiza automáticamente el validation_status del ContractFieldValue.
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReviewResource {

    private static final Set<String> VALID_TASK_STATUSES =
        Set.of("OPEN", "IN_PROGRESS", "DONE", "CANCELLED");

    private static final Set<String> VALID_ACTION_TYPES =
        Set.of("APPROVE", "REJECT", "EDIT", "OVERRIDE");

    // ── REVIEW TASKS ──────────────────────────────────────────────────────────

    @POST @Path("/review-tasks") @Transactional
    public ReviewTask createTask(CreateReviewTaskRequest req) {
        ReviewTask t = new ReviewTask();
        t.contractVersionId = req.contractVersionId;
        t.clientId          = req.clientId;
        t.assignedTo        = req.assignedTo;
        t.priority          = req.priority != null ? req.priority.toUpperCase() : "MEDIUM";
        t.dueAt             = req.dueAt;
        t.status            = "OPEN";
        t.persist();
        return t;
    }

    @GET @Path("/review-tasks")
    public List<ReviewTask> listTasks(@QueryParam("clientId")          Long   clientId,
                                      @QueryParam("contractVersionId")  Long   versionId,
                                      @QueryParam("status")             String status,
                                      @QueryParam("assignedTo")         String assignedTo) {
        var params = new java.util.ArrayList<>();
        String query = "1=1";

        if (clientId  != null) { query += " and clientId = ?"          + (params.size()+1); params.add(clientId);  }
        if (versionId != null) { query += " and contractVersionId = ?"  + (params.size()+1); params.add(versionId); }
        if (status    != null && !status.isBlank())
                               { query += " and upper(status) = ?"      + (params.size()+1); params.add(status.toUpperCase()); }
        if (assignedTo!= null && !assignedTo.isBlank())
                               { query += " and lower(assignedTo) = ?"  + (params.size()+1); params.add(assignedTo.toLowerCase()); }

        return ReviewTask.list(query, params.toArray());
    }

    @GET @Path("/review-tasks/{id}")
    public ReviewTask getTask(@PathParam("id") Long id) {
        ReviewTask t = ReviewTask.findById(id);
        if (t == null) throw new NotFoundException("ReviewTask not found");
        return t;
    }

    @PUT @Path("/review-tasks/{id}") @Transactional
    public ReviewTask updateTask(@PathParam("id") Long id,
                                 UpdateReviewTaskRequest req) {
        ReviewTask t = ReviewTask.findById(id);
        if (t == null) throw new NotFoundException("ReviewTask not found");
        if (req.assignedTo   != null) t.assignedTo   = req.assignedTo;
        if (req.priority     != null) t.priority     = req.priority.toUpperCase();
        if (req.dueAt        != null) t.dueAt        = req.dueAt;
        if (req.completedAt  != null) t.completedAt  = req.completedAt;
        if (req.status       != null) {
            if (!VALID_TASK_STATUSES.contains(req.status.toUpperCase()))
                throw new BadRequestException("Invalid status. Valid: " + VALID_TASK_STATUSES);
            t.status = req.status.toUpperCase();
            if ("DONE".equals(t.status) && t.completedAt == null)
                t.completedAt = LocalDateTime.now();
        }
        return t;
    }

    /** PATCH /review-tasks/{id}/status — transición rápida de estado */
    @PATCH @Path("/review-tasks/{id}/status") @Transactional
    public ReviewTask changeStatus(@PathParam("id") Long id,
                                   StatusRequest req) {
        ReviewTask t = ReviewTask.findById(id);
        if (t == null) throw new NotFoundException("ReviewTask not found");
        String s = req.status != null ? req.status.toUpperCase() : "";
        if (!VALID_TASK_STATUSES.contains(s))
            throw new BadRequestException("Invalid status. Valid: " + VALID_TASK_STATUSES);
        t.status = s;
        if ("DONE".equals(s) && t.completedAt == null)
            t.completedAt = LocalDateTime.now();
        return t;
    }

    @DELETE @Path("/review-tasks/{id}") @Transactional
    public void deleteTask(@PathParam("id") Long id) {
        if (!ReviewTask.deleteById(id)) throw new NotFoundException("ReviewTask not found");
    }

    // ── REVIEW ACTIONS ────────────────────────────────────────────────────────

    @POST @Path("/review-actions") @Transactional
    public ReviewAction createAction(CreateReviewActionRequest req) {

        String type = req.actionType != null ? req.actionType.toUpperCase() : "";
        if (!VALID_ACTION_TYPES.contains(type))
            throw new BadRequestException("Invalid actionType. Valid: " + VALID_ACTION_TYPES);

        ReviewAction a = new ReviewAction();
        a.contractVersionId = req.contractVersionId;
        a.fieldValueId      = req.fieldValueId;
        a.clientId          = req.clientId;
        a.actionType        = type;
        a.oldValueJson      = req.oldValueJson;
        a.newValueJson      = req.newValueJson;
        a.commentText       = req.commentText;
        a.actor             = req.actor;
        a.persist();

        // ── Side-effect: actualizar ContractFieldValue ────────────────────────
        if (req.fieldValueId != null) {
            ContractFieldValue fv = ContractFieldValue.findById(req.fieldValueId);
            if (fv != null) {
                switch (type) {
                    case "APPROVE" -> {
                        fv.validationStatus = "VALIDATED";
                        fv.reviewedBy       = req.actor;
                        fv.reviewedAt       = a.createdAt;
                        fv.reviewComment    = req.commentText;
                    }
                    case "REJECT" -> {
                        fv.validationStatus = "REJECTED";
                        fv.reviewedBy       = req.actor;
                        fv.reviewedAt       = a.createdAt;
                        fv.reviewComment    = req.commentText;
                    }
                    case "OVERRIDE" -> {
                        fv.validationStatus = "OVERRIDDEN";
                        fv.reviewedBy       = req.actor;
                        fv.reviewedAt       = a.createdAt;
                        fv.reviewComment    = req.commentText;
                        if (req.newValueJson != null) fv.valueJson = req.newValueJson;
                    }
                    default -> {}
                }
            }
        }

        return a;
    }

    @GET @Path("/review-actions")
    public List<ReviewAction> listActions(@QueryParam("clientId")         Long   clientId,
                                          @QueryParam("contractVersionId") Long   versionId,
                                          @QueryParam("fieldValueId")      Long   fieldValueId,
                                          @QueryParam("actionType")        String actionType,
                                          @QueryParam("actor")             String actor) {
        var params = new java.util.ArrayList<>();
        String query = "1=1";

        if (clientId     != null) { query += " and clientId = ?"          + (params.size()+1); params.add(clientId);     }
        if (versionId    != null) { query += " and contractVersionId = ?"  + (params.size()+1); params.add(versionId);    }
        if (fieldValueId != null) { query += " and fieldValueId = ?"       + (params.size()+1); params.add(fieldValueId); }
        if (actionType   != null && !actionType.isBlank())
                                  { query += " and upper(actionType) = ?"  + (params.size()+1); params.add(actionType.toUpperCase()); }
        if (actor        != null && !actor.isBlank())
                                  { query += " and lower(actor) = ?"       + (params.size()+1); params.add(actor.toLowerCase()); }

        return ReviewAction.list(query, params.toArray());
    }

    @GET @Path("/review-actions/{id}")
    public ReviewAction getAction(@PathParam("id") Long id) {
        ReviewAction a = ReviewAction.findById(id);
        if (a == null) throw new NotFoundException("ReviewAction not found");
        return a;
    }

    @DELETE @Path("/review-actions/{id}") @Transactional
    public void deleteAction(@PathParam("id") Long id) {
        if (!ReviewAction.deleteById(id)) throw new NotFoundException("ReviewAction not found");
    }

    // ── Inner DTO para PATCH /status ──────────────────────────────────────────
    public static class StatusRequest {
        public String status;
    }
}
