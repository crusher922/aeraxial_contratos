package com.aeraxial.contracts.api;

import com.aeraxial.contracts.domain.*;
import com.aeraxial.contracts.dto.*;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

/**
 * CRUD para las 3 tablas legales de un contrato.
 *
 *  POST/GET        /contracts/{contractId}/clauses
 *  GET/PUT/DELETE  /contracts/{contractId}/clauses/{id}
 *
 *  POST/GET        /contracts/{contractId}/obligations
 *  GET/PUT/DELETE  /contracts/{contractId}/obligations/{id}
 *
 *  POST/GET        /contracts/{contractId}/insurance
 *  GET/PUT/DELETE  /contracts/{contractId}/insurance/{id}
 */
@Path("/contracts/{contractId}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LegalResource {

    // ── CLÁUSULAS ─────────────────────────────────────────────────────────────

    @POST @Path("/clauses") @Transactional
    public Clause createClause(@PathParam("contractId") Long contractId,
                               CreateClauseRequest req) {
        Clause c = new Clause();
        c.contractId = contractId;
        c.clauseType = req.clauseType;
        c.title      = req.title;
        c.details    = req.details;
        c.isCritical = req.isCritical != null ? req.isCritical.toUpperCase() : "N";
        c.persist();
        return c;
    }

    @GET @Path("/clauses")
    public List<Clause> listClauses(@PathParam("contractId") Long contractId,
                                    @QueryParam("clauseType") String clauseType,
                                    @QueryParam("isCritical") String isCritical) {
        var params = new java.util.ArrayList<>();
        String query = "contractId = ?1";
        params.add(contractId);

        if (clauseType != null && !clauseType.isBlank()) {
            query += " and upper(clauseType) = ?" + (params.size() + 1);
            params.add(clauseType.toUpperCase());
        }
        if (isCritical != null && !isCritical.isBlank()) {
            query += " and upper(isCritical) = ?" + (params.size() + 1);
            params.add(isCritical.toUpperCase());
        }
        return Clause.list(query, params.toArray());
    }

    @GET @Path("/clauses/{id}")
    public Clause getClause(@PathParam("contractId") Long contractId,
                            @PathParam("id") Long id) {
        Clause c = Clause.findById(id);
        if (c == null || !c.contractId.equals(contractId))
            throw new NotFoundException("Clause not found");
        return c;
    }

    @PUT @Path("/clauses/{id}") @Transactional
    public Clause updateClause(@PathParam("contractId") Long contractId,
                               @PathParam("id") Long id,
                               CreateClauseRequest req) {
        Clause c = Clause.findById(id);
        if (c == null || !c.contractId.equals(contractId))
            throw new NotFoundException("Clause not found");
        if (req.clauseType != null) c.clauseType = req.clauseType;
        if (req.title      != null) c.title      = req.title;
        if (req.details    != null) c.details    = req.details;
        if (req.isCritical != null) c.isCritical = req.isCritical.toUpperCase();
        return c;
    }

    @DELETE @Path("/clauses/{id}") @Transactional
    public void deleteClause(@PathParam("contractId") Long contractId,
                             @PathParam("id") Long id) {
        Clause c = Clause.findById(id);
        if (c == null || !c.contractId.equals(contractId))
            throw new NotFoundException("Clause not found");
        c.delete();
    }

    // ── OBLIGACIONES ──────────────────────────────────────────────────────────

    @POST @Path("/obligations") @Transactional
    public TenantObligation createObligation(@PathParam("contractId") Long contractId,
                                             CreateObligationRequest req) {
        TenantObligation o = new TenantObligation();
        o.contractId     = contractId;
        o.obligationType = req.obligationType;
        o.details        = req.details;
        o.persist();
        return o;
    }

    @GET @Path("/obligations")
    public List<TenantObligation> listObligations(@PathParam("contractId") Long contractId,
                                                  @QueryParam("obligationType") String type) {
        if (type != null && !type.isBlank())
            return TenantObligation.list(
                "contractId = ?1 and upper(obligationType) = ?2",
                contractId, type.toUpperCase());
        return TenantObligation.list("contractId", contractId);
    }

    @GET @Path("/obligations/{id}")
    public TenantObligation getObligation(@PathParam("contractId") Long contractId,
                                          @PathParam("id") Long id) {
        TenantObligation o = TenantObligation.findById(id);
        if (o == null || !o.contractId.equals(contractId))
            throw new NotFoundException("Obligation not found");
        return o;
    }

    @PUT @Path("/obligations/{id}") @Transactional
    public TenantObligation updateObligation(@PathParam("contractId") Long contractId,
                                             @PathParam("id") Long id,
                                             CreateObligationRequest req) {
        TenantObligation o = TenantObligation.findById(id);
        if (o == null || !o.contractId.equals(contractId))
            throw new NotFoundException("Obligation not found");
        if (req.obligationType != null) o.obligationType = req.obligationType;
        if (req.details        != null) o.details        = req.details;
        return o;
    }

    @DELETE @Path("/obligations/{id}") @Transactional
    public void deleteObligation(@PathParam("contractId") Long contractId,
                                 @PathParam("id") Long id) {
        TenantObligation o = TenantObligation.findById(id);
        if (o == null || !o.contractId.equals(contractId))
            throw new NotFoundException("Obligation not found");
        o.delete();
    }

    // ── SEGUROS REQUERIDOS ────────────────────────────────────────────────────

    @POST @Path("/insurance") @Transactional
    public InsuranceRequired createInsurance(@PathParam("contractId") Long contractId,
                                             CreateInsuranceRequest req) {
        InsuranceRequired ins = new InsuranceRequired();
        ins.contractId     = contractId;
        ins.insuranceType  = req.insuranceType;
        ins.coverageAmount = req.coverageAmount;
        ins.currency       = req.currency;
        ins.validFrom      = req.validFrom;
        ins.validTo        = req.validTo;
        ins.notes          = req.notes;
        ins.persist();
        return ins;
    }

    @GET @Path("/insurance")
    public List<InsuranceRequired> listInsurance(@PathParam("contractId") Long contractId,
                                                 @QueryParam("insuranceType") String type) {
        if (type != null && !type.isBlank())
            return InsuranceRequired.list(
                "contractId = ?1 and upper(insuranceType) = ?2",
                contractId, type.toUpperCase());
        return InsuranceRequired.list("contractId", contractId);
    }

    @GET @Path("/insurance/{id}")
    public InsuranceRequired getInsurance(@PathParam("contractId") Long contractId,
                                          @PathParam("id") Long id) {
        InsuranceRequired ins = InsuranceRequired.findById(id);
        if (ins == null || !ins.contractId.equals(contractId))
            throw new NotFoundException("Insurance not found");
        return ins;
    }

    @PUT @Path("/insurance/{id}") @Transactional
    public InsuranceRequired updateInsurance(@PathParam("contractId") Long contractId,
                                             @PathParam("id") Long id,
                                             CreateInsuranceRequest req) {
        InsuranceRequired ins = InsuranceRequired.findById(id);
        if (ins == null || !ins.contractId.equals(contractId))
            throw new NotFoundException("Insurance not found");
        if (req.insuranceType  != null) ins.insuranceType  = req.insuranceType;
        if (req.coverageAmount != null) ins.coverageAmount = req.coverageAmount;
        if (req.currency       != null) ins.currency       = req.currency;
        if (req.validFrom      != null) ins.validFrom      = req.validFrom;
        if (req.validTo        != null) ins.validTo        = req.validTo;
        if (req.notes          != null) ins.notes          = req.notes;
        return ins;
    }

    @DELETE @Path("/insurance/{id}") @Transactional
    public void deleteInsurance(@PathParam("contractId") Long contractId,
                                @PathParam("id") Long id) {
        InsuranceRequired ins = InsuranceRequired.findById(id);
        if (ins == null || !ins.contractId.equals(contractId))
            throw new NotFoundException("Insurance not found");
        ins.delete();
    }
}
