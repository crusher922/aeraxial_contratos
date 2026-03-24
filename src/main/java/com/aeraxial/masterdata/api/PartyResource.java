package com.aeraxial.masterdata.api;

import com.aeraxial.masterdata.domain.Party;
import com.aeraxial.masterdata.dto.CreatePartyRequest;
import com.aeraxial.masterdata.dto.UpdatePartyRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.awt.*;
import java.util.List;

@Path("/parties")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PartyResource {

    @POST
    @Transactional
    public Party create(CreatePartyRequest req){
        Party p = new Party();
        p.clientId = req.clientId;
        p.partyType = req.partyType;
        p.legalName = req.legalName;
        p.tradeName = req.tradeName;
        p.taxId = req.taxId;
        p.econActivity = req.econActivity;
        p.businessCategory = req.businessCategory;
        p.contactName = req.contactName;
        p.contactEmail = req.contactEmail;
        p.contactPhone = req.contactPhone;
        p.legalRepresentative = req.legalRepresentative;
        p.persist();
        return p;
    }

    @GET
    public List<Party> list(@QueryParam("clientId") Long clientId,
                            @QueryParam("partyType") String partyType,
                            @QueryParam("search") String search){
        String query = "1=1";
        var params = new java.util.ArrayList<>();

        if (clientId != null) {
            query += " and clientId = ?" + (params.size() + 1);
            params.add(clientId);
        }
        if (partyType != null && !partyType.isBlank()) {
            query += " and upper(partyType) = ?" + (params.size() + 1);
            params.add(partyType.toUpperCase());
        }
        if (search != null && !search.isBlank()) {
            query += " and lower(legalName) like ?" + (params.size() + 1)
                    + " or lower(tradeName) like ?" + (params.size() + 2)
                    + " or lower(taxId) like ?" +(params.size() + 3) + ")";
            String s = "%" + search.toLowerCase() + "%";
            params.add(s);
            params.add(s);
            params.add(s);
        }
        return Party.list(query, params.toArray());
    }

    @GET
    @Path("/{id}")
    public  Party get(@PathParam("id") Long id){
        Party p = Party.findById(id);
        if ( p == null) throw new  NotFoundException("Party not found");
        return p;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Party update(@PathParam("id") Long id, UpdatePartyRequest req){
        Party p = Party.findById(id);
        if ( p == null) throw new NotFoundException("Party not found");

        p.partyType = req.partyType;
        p.legalName = req.legalName;
        p.tradeName = req.tradeName;
        p.taxId = req.taxId;
        p.econActivity = req.econActivity;
        p.businessCategory = req.businessCategory;
        p.contactName = req.contactName;
        p.contactEmail = req.contactEmail;
        p.contactPhone = req.contactPhone;
        p.legalRepresentative = req.legalRepresentative;
        return p;
    }
}
