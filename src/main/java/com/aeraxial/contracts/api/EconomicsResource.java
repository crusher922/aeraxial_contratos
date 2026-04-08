package com.aeraxial.contracts.api;

import com.aeraxial.contracts.domain.*;
import com.aeraxial.contracts.dto.*;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

/**
 * CRUD para las 6 tablas económicas de un contrato.
 *
 *  POST/GET        /contracts/{contractId}/economics/rent-base
 *  GET/PUT/DELETE  /contracts/{contractId}/economics/rent-base/{id}
 *  (mismo patrón para: rent-variable, rent-escalation,
 *   security-deposit, additional-charges, payment-terms)
 */
@Path("/contracts/{contractId}/economics")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EconomicsResource {

    // ── RENT BASE ─────────────────────────────────────────────────────────────

    @POST @Path("/rent-base") @Transactional
    public RentBase createRentBase(@PathParam("contractId") Long contractId,
                                   CreateRentBaseRequest req) {
        RentBase rb = new RentBase();
        rb.contractId    = contractId;
        rb.amount        = req.amount;
        rb.currency      = req.currency    != null ? req.currency    : "USD";
        rb.periodicity   = req.periodicity != null ? req.periodicity : "MONTHLY";
        rb.paymentDueDay = req.paymentDueDay;
        rb.persist();
        return rb;
    }

    @GET @Path("/rent-base")
    public List<RentBase> listRentBase(@PathParam("contractId") Long contractId) {
        return RentBase.list("contractId", contractId);
    }

    @GET @Path("/rent-base/{id}")
    public RentBase getRentBase(@PathParam("contractId") Long contractId,
                                @PathParam("id") Long id) {
        RentBase rb = RentBase.findById(id);
        if (rb == null || !rb.contractId.equals(contractId))
            throw new NotFoundException("RentBase not found");
        return rb;
    }

    @PUT @Path("/rent-base/{id}") @Transactional
    public RentBase updateRentBase(@PathParam("contractId") Long contractId,
                                   @PathParam("id") Long id,
                                   CreateRentBaseRequest req) {
        RentBase rb = RentBase.findById(id);
        if (rb == null || !rb.contractId.equals(contractId))
            throw new NotFoundException("RentBase not found");
        if (req.amount       != null) rb.amount        = req.amount;
        if (req.currency     != null) rb.currency      = req.currency;
        if (req.periodicity  != null) rb.periodicity   = req.periodicity;
        if (req.paymentDueDay!= null) rb.paymentDueDay = req.paymentDueDay;
        return rb;
    }

    @DELETE @Path("/rent-base/{id}") @Transactional
    public void deleteRentBase(@PathParam("contractId") Long contractId,
                               @PathParam("id") Long id) {
        RentBase rb = RentBase.findById(id);
        if (rb == null || !rb.contractId.equals(contractId))
            throw new NotFoundException("RentBase not found");
        rb.delete();
    }

    // ── RENT VARIABLE ─────────────────────────────────────────────────────────

    @POST @Path("/rent-variable") @Transactional
    public RentVariable createRentVariable(@PathParam("contractId") Long contractId,
                                           CreateRentVariableRequest req) {
        RentVariable rv = new RentVariable();
        rv.contractId           = contractId;
        rv.percentage           = req.percentage;
        rv.thresholdJson        = req.thresholdJson;
        rv.minGuaranteeAmount   = req.minGuaranteeAmount;
        rv.minGuaranteeCurrency = req.minGuaranteeCurrency;
        rv.notes                = req.notes;
        rv.persist();
        return rv;
    }

    @GET @Path("/rent-variable")
    public List<RentVariable> listRentVariable(@PathParam("contractId") Long contractId) {
        return RentVariable.list("contractId", contractId);
    }

    @GET @Path("/rent-variable/{id}")
    public RentVariable getRentVariable(@PathParam("contractId") Long contractId,
                                        @PathParam("id") Long id) {
        RentVariable rv = RentVariable.findById(id);
        if (rv == null || !rv.contractId.equals(contractId))
            throw new NotFoundException("RentVariable not found");
        return rv;
    }

    @PUT @Path("/rent-variable/{id}") @Transactional
    public RentVariable updateRentVariable(@PathParam("contractId") Long contractId,
                                           @PathParam("id") Long id,
                                           CreateRentVariableRequest req) {
        RentVariable rv = RentVariable.findById(id);
        if (rv == null || !rv.contractId.equals(contractId))
            throw new NotFoundException("RentVariable not found");
        if (req.percentage           != null) rv.percentage           = req.percentage;
        if (req.thresholdJson        != null) rv.thresholdJson        = req.thresholdJson;
        if (req.minGuaranteeAmount   != null) rv.minGuaranteeAmount   = req.minGuaranteeAmount;
        if (req.minGuaranteeCurrency != null) rv.minGuaranteeCurrency = req.minGuaranteeCurrency;
        if (req.notes                != null) rv.notes                = req.notes;
        return rv;
    }

    @DELETE @Path("/rent-variable/{id}") @Transactional
    public void deleteRentVariable(@PathParam("contractId") Long contractId,
                                   @PathParam("id") Long id) {
        RentVariable rv = RentVariable.findById(id);
        if (rv == null || !rv.contractId.equals(contractId))
            throw new NotFoundException("RentVariable not found");
        rv.delete();
    }

    // ── RENT ESCALATION ───────────────────────────────────────────────────────

    @POST @Path("/rent-escalation") @Transactional
    public RentEscalation createRentEscalation(@PathParam("contractId") Long contractId,
                                               CreateRentEscalationRequest req) {
        RentEscalation re = new RentEscalation();
        re.contractId  = contractId;
        re.formula     = req.formula;
        re.indexName   = req.indexName;
        re.periodicity = req.periodicity;
        re.extra1      = req.extra1;
        re.extra2      = req.extra2;
        re.extra3      = req.extra3;
        re.extra4      = req.extra4;
        re.extra5      = req.extra5;
        re.notes       = req.notes;
        re.persist();
        return re;
    }

    @GET @Path("/rent-escalation")
    public List<RentEscalation> listRentEscalation(@PathParam("contractId") Long contractId) {
        return RentEscalation.list("contractId", contractId);
    }

    @GET @Path("/rent-escalation/{id}")
    public RentEscalation getRentEscalation(@PathParam("contractId") Long contractId,
                                            @PathParam("id") Long id) {
        RentEscalation re = RentEscalation.findById(id);
        if (re == null || !re.contractId.equals(contractId))
            throw new NotFoundException("RentEscalation not found");
        return re;
    }

    @PUT @Path("/rent-escalation/{id}") @Transactional
    public RentEscalation updateRentEscalation(@PathParam("contractId") Long contractId,
                                               @PathParam("id") Long id,
                                               CreateRentEscalationRequest req) {
        RentEscalation re = RentEscalation.findById(id);
        if (re == null || !re.contractId.equals(contractId))
            throw new NotFoundException("RentEscalation not found");
        if (req.formula     != null) re.formula     = req.formula;
        if (req.indexName   != null) re.indexName   = req.indexName;
        if (req.periodicity != null) re.periodicity = req.periodicity;
        if (req.extra1      != null) re.extra1      = req.extra1;
        if (req.extra2      != null) re.extra2      = req.extra2;
        if (req.extra3      != null) re.extra3      = req.extra3;
        if (req.extra4      != null) re.extra4      = req.extra4;
        if (req.extra5      != null) re.extra5      = req.extra5;
        if (req.notes       != null) re.notes       = req.notes;
        return re;
    }

    @DELETE @Path("/rent-escalation/{id}") @Transactional
    public void deleteRentEscalation(@PathParam("contractId") Long contractId,
                                     @PathParam("id") Long id) {
        RentEscalation re = RentEscalation.findById(id);
        if (re == null || !re.contractId.equals(contractId))
            throw new NotFoundException("RentEscalation not found");
        re.delete();
    }

    // ── SECURITY DEPOSIT ──────────────────────────────────────────────────────

    @POST @Path("/security-deposit") @Transactional
    public SecurityDeposit createSecurityDeposit(@PathParam("contractId") Long contractId,
                                                 CreateSecurityDepositRequest req) {
        SecurityDeposit sd = new SecurityDeposit();
        sd.contractId       = contractId;
        sd.amount           = req.amount;
        sd.currency         = req.currency != null ? req.currency : "USD";
        sd.dueDate          = req.dueDate;
        sd.refundConditions = req.refundConditions;
        sd.persist();
        return sd;
    }

    @GET @Path("/security-deposit")
    public List<SecurityDeposit> listSecurityDeposit(@PathParam("contractId") Long contractId) {
        return SecurityDeposit.list("contractId", contractId);
    }

    @GET @Path("/security-deposit/{id}")
    public SecurityDeposit getSecurityDeposit(@PathParam("contractId") Long contractId,
                                              @PathParam("id") Long id) {
        SecurityDeposit sd = SecurityDeposit.findById(id);
        if (sd == null || !sd.contractId.equals(contractId))
            throw new NotFoundException("SecurityDeposit not found");
        return sd;
    }

    @PUT @Path("/security-deposit/{id}") @Transactional
    public SecurityDeposit updateSecurityDeposit(@PathParam("contractId") Long contractId,
                                                 @PathParam("id") Long id,
                                                 CreateSecurityDepositRequest req) {
        SecurityDeposit sd = SecurityDeposit.findById(id);
        if (sd == null || !sd.contractId.equals(contractId))
            throw new NotFoundException("SecurityDeposit not found");
        if (req.amount           != null) sd.amount           = req.amount;
        if (req.currency         != null) sd.currency         = req.currency;
        if (req.dueDate          != null) sd.dueDate          = req.dueDate;
        if (req.refundConditions != null) sd.refundConditions = req.refundConditions;
        return sd;
    }

    @DELETE @Path("/security-deposit/{id}") @Transactional
    public void deleteSecurityDeposit(@PathParam("contractId") Long contractId,
                                      @PathParam("id") Long id) {
        SecurityDeposit sd = SecurityDeposit.findById(id);
        if (sd == null || !sd.contractId.equals(contractId))
            throw new NotFoundException("SecurityDeposit not found");
        sd.delete();
    }

    // ── ADDITIONAL CHARGES ────────────────────────────────────────────────────

    @POST @Path("/additional-charges") @Transactional
    public AdditionalCharges createCharge(@PathParam("contractId") Long contractId,
                                          CreateAdditionalChargeRequest req) {
        AdditionalCharges ac = new AdditionalCharges();
        ac.contractId  = contractId;
        ac.chargeType  = req.chargeType;
        ac.amount      = req.amount;
        ac.currency    = req.currency    != null ? req.currency : "USD";
        ac.periodicity = req.periodicity;
        ac.notes       = req.notes;
        ac.persist();
        return ac;
    }

    @GET @Path("/additional-charges")
    public List<AdditionalCharges> listCharges(@PathParam("contractId") Long contractId,
                                               @QueryParam("chargeType") String chargeType) {
        if (chargeType != null && !chargeType.isBlank())
            return AdditionalCharges.list(
                "contractId = ?1 and upper(chargeType) = ?2",
                contractId, chargeType.toUpperCase());
        return AdditionalCharges.list("contractId", contractId);
    }

    @GET @Path("/additional-charges/{id}")
    public AdditionalCharges getCharge(@PathParam("contractId") Long contractId,
                                       @PathParam("id") Long id) {
        AdditionalCharges ac = AdditionalCharges.findById(id);
        if (ac == null || !ac.contractId.equals(contractId))
            throw new NotFoundException("AdditionalCharge not found");
        return ac;
    }

    @PUT @Path("/additional-charges/{id}") @Transactional
    public AdditionalCharges updateCharge(@PathParam("contractId") Long contractId,
                                          @PathParam("id") Long id,
                                          CreateAdditionalChargeRequest req) {
        AdditionalCharges ac = AdditionalCharges.findById(id);
        if (ac == null || !ac.contractId.equals(contractId))
            throw new NotFoundException("AdditionalCharge not found");
        if (req.chargeType  != null) ac.chargeType  = req.chargeType;
        if (req.amount      != null) ac.amount      = req.amount;
        if (req.currency    != null) ac.currency    = req.currency;
        if (req.periodicity != null) ac.periodicity = req.periodicity;
        if (req.notes       != null) ac.notes       = req.notes;
        return ac;
    }

    @DELETE @Path("/additional-charges/{id}") @Transactional
    public void deleteCharge(@PathParam("contractId") Long contractId,
                             @PathParam("id") Long id) {
        AdditionalCharges ac = AdditionalCharges.findById(id);
        if (ac == null || !ac.contractId.equals(contractId))
            throw new NotFoundException("AdditionalCharge not found");
        ac.delete();
    }

    // ── PAYMENT TERMS ─────────────────────────────────────────────────────────

    @POST @Path("/payment-terms") @Transactional
    public PaymentTerms createPaymentTerms(@PathParam("contractId") Long contractId,
                                           CreatePaymentTermsRequest req) {
        PaymentTerms pt = new PaymentTerms();
        pt.contractId          = contractId;
        pt.paymentMethod       = req.paymentMethod;
        pt.paymentTermsText    = req.paymentTermsText;
        pt.gracePeriodDays     = req.gracePeriodDays;
        pt.lateFeeType         = req.lateFeeType;
        pt.lateFeeAmount       = req.lateFeeAmount;
        pt.lateFeePercent      = req.lateFeePercent;
        pt.lateInterestPercent = req.lateInterestPercent;
        pt.persist();
        return pt;
    }

    @GET @Path("/payment-terms")
    public List<PaymentTerms> listPaymentTerms(@PathParam("contractId") Long contractId) {
        return PaymentTerms.list("contractId", contractId);
    }

    @GET @Path("/payment-terms/{id}")
    public PaymentTerms getPaymentTerms(@PathParam("contractId") Long contractId,
                                        @PathParam("id") Long id) {
        PaymentTerms pt = PaymentTerms.findById(id);
        if (pt == null || !pt.contractId.equals(contractId))
            throw new NotFoundException("PaymentTerms not found");
        return pt;
    }

    @PUT @Path("/payment-terms/{id}") @Transactional
    public PaymentTerms updatePaymentTerms(@PathParam("contractId") Long contractId,
                                           @PathParam("id") Long id,
                                           CreatePaymentTermsRequest req) {
        PaymentTerms pt = PaymentTerms.findById(id);
        if (pt == null || !pt.contractId.equals(contractId))
            throw new NotFoundException("PaymentTerms not found");
        if (req.paymentMethod       != null) pt.paymentMethod       = req.paymentMethod;
        if (req.paymentTermsText    != null) pt.paymentTermsText    = req.paymentTermsText;
        if (req.gracePeriodDays     != null) pt.gracePeriodDays     = req.gracePeriodDays;
        if (req.lateFeeType         != null) pt.lateFeeType         = req.lateFeeType;
        if (req.lateFeeAmount       != null) pt.lateFeeAmount       = req.lateFeeAmount;
        if (req.lateFeePercent      != null) pt.lateFeePercent      = req.lateFeePercent;
        if (req.lateInterestPercent != null) pt.lateInterestPercent = req.lateInterestPercent;
        return pt;
    }

    @DELETE @Path("/payment-terms/{id}") @Transactional
    public void deletePaymentTerms(@PathParam("contractId") Long contractId,
                                   @PathParam("id") Long id) {
        PaymentTerms pt = PaymentTerms.findById(id);
        if (pt == null || !pt.contractId.equals(contractId))
            throw new NotFoundException("PaymentTerms not found");
        pt.delete();
    }
}
