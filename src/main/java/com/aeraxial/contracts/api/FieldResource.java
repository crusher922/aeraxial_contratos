package com.aeraxial.contracts.api;

import com.aeraxial.contracts.domain.ContractFieldValue;
import com.aeraxial.contracts.domain.EvidenceSnippet;
import com.aeraxial.contracts.dto.*;
import com.aeraxial.contracts.service.FieldService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FieldResource {

    @Inject FieldService service;

    @GET
    @Path("/versions/{versionId}/field-values")
    public List<ContractFieldValue> list(@HeaderParam("X-Client-Id") String clientIdHeader,
                                         @PathParam("versionId") long versionId) {
        long clientId = RequestContext.requireClientId(clientIdHeader);
        return service.listValues(clientId, versionId);
    }

    @POST
    @Path("/versions/{versionId}/field-values:bulk-upsert")
    public List<ContractFieldValue> bulkUpsert(@HeaderParam("X-Client-Id") String clientIdHeader,
                                               @HeaderParam("X-Actor") String actorHeader,
                                               @PathParam("versionId") long versionId,
                                               @Valid BulkUpsertFieldValuesRequest req) {
        long clientId = RequestContext.requireClientId(clientIdHeader);
        String actor = RequestContext.actorOrSystem(actorHeader);
        return service.bulkUpsert(clientId, actor, versionId, req);
    }

    @POST
    @Path("/field-values/{fieldValueId}/evidence")
    public EvidenceSnippet addEvidence(@HeaderParam("X-Client-Id") String clientIdHeader,
                                       @HeaderParam("X-Actor") String actorHeader,
                                       @PathParam("fieldValueId") long fieldValueId,
                                       CreateEvidenceRequest req) {
        long clientId = RequestContext.requireClientId(clientIdHeader);
        String actor = RequestContext.actorOrSystem(actorHeader);
        return service.addEvidence(clientId, actor, fieldValueId, req);
    }

    @PATCH
    @Path("/field-values/{fieldValueId}/validation")
    public ContractFieldValue updateValidation(@HeaderParam("X-Client-Id") String clientIdHeader,
                                               @HeaderParam("X-Actor") String actorHeader,
                                               @PathParam("fieldValueId") long fieldValueId,
                                               @Valid UpdateValidationStatusRequest req) {
        long clientId = RequestContext.requireClientId(clientIdHeader);
        String actor = RequestContext.actorOrSystem(actorHeader);
        return service.updateValidationStatus(clientId, actor, fieldValueId, req);
    }
}