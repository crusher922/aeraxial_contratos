package com.aeraxial.masterdata.api;

import com.aeraxial.masterdata.domain.ContractSpace;
import com.aeraxial.masterdata.dto.CreateContractSpaceRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/contracts/{contractId}/spaces")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ContractSpaceResource {

    @POST
    @Transactional
    public ContractSpace create(@PathParam("contractId") Long contractId,
                                CreateContractSpaceRequest req){
        ContractSpace cs = new  ContractSpace();
        cs.cotractId = contractId;
        cs.spaceId = req.spaceId;
        cs.effectiveFrom = req.effectiveFrom;
        cs.effectiveTo = req.effectiveTo;
        cs.persist();
        return cs;
    }

    @GET
    public List<ContractSpace> list(@PathParam("contractId") Long contractId){
        return ContractSpace.list("contractId", contractId);
    }

    @DELETE
    @Path("/{contractspaceId}")
    @Transactional
    public void delete(@PathParam("contractspaceId") Long contractspaceId){
        boolean deleted = ContractSpace.deleteById(contractspaceId);
        if (!deleted) throw new NotFoundException("Contract space not found");
    }
}
