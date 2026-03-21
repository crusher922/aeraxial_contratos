package com.aeraxial.masterdata.api;

import com.aeraxial.contracts.domain.Contract;
import com.aeraxial.masterdata.domain.ContractParty;
import com.aeraxial.masterdata.dto.CreateContractPartyRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/contracts/{contractId}/parties")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ContractPartyResource {

    @POST
    @Transactional
    public ContractParty create(@PathParam("contractId")Long contractId,
                                CreateContractPartyRequest req){
        ContractParty cp = new  ContractParty();
        cp.contractId = contractId;
        cp.partyId = req.partyId;
        cp.role = req.role;
        cp.isPrimary = req.isPrimary == null ? "N" : req.isPrimary;
        cp.persist();
        return cp;
    }

    @GET
    public List<ContractParty> list(@PathParam("contractId")Long contractId){
        return ContractParty.list("contractId",contractId);
    }

    @DELETE
    @Path("/{contractPartyId}")
    @Transactional
    public void delete(@PathParam("contractPartyId")Long contractPartyId){
        boolean deleted = ContractParty.deleteById(contractPartyId);
        if (!deleted) throw new NotFoundException("Contract party not found");
    }
}
