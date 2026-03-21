package com.aeraxial.masterdata.api;

import com.aeraxial.masterdata.domain.LeasedSpace;
import com.aeraxial.masterdata.dto.CreateSpaceRequest;
import com.aeraxial.masterdata.dto.UpdateSpaceRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/spaces")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SpaceResource {

    @POST
    @Transactional
    public LeasedSpace create(CreateSpaceRequest req){
        LeasedSpace s = new  LeasedSpace();
        s.clientId = req.clientId;
        s.siteId = req.siteId;
        s.locationId = req.locationId;
        s.spaceCode = req.spaceCode;
        s.areaValue = req.areaValue;
        s.areaUnit = req.areaUnit;
        s.spaceType = req.spaceType;
        s.description = req.description;
        s.territorialExclusivity = req.territorialExclusivity == null ? "N" : req.territorialExclusivity;
        s.exclusivityNotes = req.exclusivityNotes;
        s.persist();
        return s;
    }

    @GET
    public List<LeasedSpace> list (@QueryParam("clientId") Long clientId,
                                   @QueryParam("siteId") Long siteId,
                                   @QueryParam("spaceType") String spaceType){
        String query = "1=1";
        var params = new java.util.ArrayList<>();

        if (clientId != null) {
            query += " and clientId = ?" + (params.size() + 1);
            params.add(clientId);
        }
        if (siteId != null) {
            query += " and siteId = ?" + (params.size() + 1);
            params.add(siteId);
        }
        if (spaceType != null && !spaceType.isBlank()) {
            query += " and upper(spaceType) = ?" + (params.size() + 1);
            params.add(spaceType.toUpperCase());
        }

        return LeasedSpace.list(query, params.toArray());
    }

    @GET
    @Path("/{id}")
    public LeasedSpace get(@PathParam("id") Long id){
        LeasedSpace s = LeasedSpace.findById(id);
        if (s == null) throw new NotFoundException("Space not found");
        return s;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public LeasedSpace update(@PathParam("id") Long id, UpdateSpaceRequest req){
        LeasedSpace s = LeasedSpace.findById(id);
        if (s == null) throw new NotFoundException("Space not found");

        s.siteId = req.siteId;
        s.locationId = req.locationId;
        s.spaceCode = req.spaceCode;
        s.areaValue = req.areaValue;
        s.areaUnit = req.areaUnit;
        s.spaceType = req.spaceType;
        s.description = req.description;
        s.territorialExclusivity = req.territorialExclusivity == null ? "N" : req.territorialExclusivity;
        s.exclusivityNotes=req.exclusivityNotes;
        return s;
    }
}
