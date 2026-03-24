package com.aeraxial.contracts.api;

import com.aeraxial.contracts.domain.Contract;
import com.aeraxial.contracts.dto.CreateContractRequest;
import com.aeraxial.contracts.dto.UpdateContractStatusRequest;
import com.aeraxial.contracts.service.ContractService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/contracts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ContractResource {

    @Inject ContractService service;

    @POST
    public Contract create(@HeaderParam("X-Client-Id") String clientIdHeader,
                           @HeaderParam("X-Actor") String actorHeader,
                           @Valid CreateContractRequest req) {
        long clientId = RequestContext.requireClientId(clientIdHeader);
        String actor = RequestContext.actorOrSystem(actorHeader);
        return service.create(clientId, actor, req);
    }

    @GET
    @Path("/all")
    public List<Contract> getAll() {
        return Contract.listAll();
    }

    @GET
    public List<Contract> search(@HeaderParam("X-Client-Id") String clientIdHeader,
                                 @QueryParam("status") String status) {
        long clientId = RequestContext.requireClientId(clientIdHeader);
        return service.search(clientId, status);
    }

    @GET
    @Path("/{id}")
    public Contract get(@HeaderParam("X-Client-Id") String clientIdHeader,
                        @PathParam("id") long id) {
        long clientId = RequestContext.requireClientId(clientIdHeader);
        return service.get(clientId, id);
    }

    @PATCH
    @Path("/{id}/status")
    public Contract updateStatus(@HeaderParam("X-Client-Id") String clientIdHeader,
                                 @HeaderParam("X-Actor") String actorHeader,
                                 @PathParam("id") long id,
                                 @Valid UpdateContractStatusRequest req) {
        long clientId = RequestContext.requireClientId(clientIdHeader);
        String actor = RequestContext.actorOrSystem(actorHeader);
        return service.updateStatus(clientId, actor, id, req);
    }
}