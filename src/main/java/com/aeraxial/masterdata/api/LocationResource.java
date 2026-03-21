package com.aeraxial.masterdata.api;

import com.aeraxial.masterdata.domain.AirportLocation;
import com.aeraxial.masterdata.dto.CreateLocationRequest;
import com.aeraxial.masterdata.dto.UpdateLocationRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/locations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LocationResource {

    @POST
    @Transactional
    public AirportLocation create(CreateLocationRequest req){
        AirportLocation l = new  AirportLocation();
        l.clientId = req.clientId;
        l.siteId = req.siteId;
        l.terminal = req.terminal;
        l.floor = req.floor;
        l.zone = req.zone;
        l.description = req.description;
        l.persist();
        return l;
    }

    @GET
    public List<AirportLocation> list(@QueryParam("clientId")Long clientId,
                                      @QueryParam("siteId") Long siteId){
        if (clientId != null && siteId != null) {
            return AirportLocation.list("clientId = ?1 and siteId = ?2", clientId, siteId);
        }

        if (clientId != null) {
            return AirportLocation.list("clientId = ?1", clientId);
        }
        return AirportLocation.listAll();
    }

    @GET
    @Path("/{id}")
    public AirportLocation get(@PathParam("id") Long id){
        AirportLocation l = AirportLocation.findById(id);
        if (l == null) throw new NotFoundException("Location not found");
        return l;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public AirportLocation update(@PathParam("id") Long id, UpdateLocationRequest req){
        AirportLocation l = AirportLocation.findById(id);
        if ( l == null) throw new  NotFoundException("Location not found");

        l.siteId = req.siteId;
        l.terminal = req.terminal;
        l.floor = req.floor;
        l.zone = req.zone;
        l.description = req.description;
        return l;
    }


}
