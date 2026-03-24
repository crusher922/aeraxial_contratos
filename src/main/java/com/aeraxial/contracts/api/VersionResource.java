package com.aeraxial.contracts.api;

import com.aeraxial.contracts.domain.ContractVersion;
import com.aeraxial.contracts.domain.DocumentObject;
import com.aeraxial.contracts.dto.CreateDocumentRequest;
import com.aeraxial.contracts.dto.CreateVersionRequest;
import com.aeraxial.contracts.dto.UpdateVersionStatusRequest;
import com.aeraxial.contracts.service.VersionService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VersionResource {

    @Inject VersionService service;

    // ---- Documentos ----
    @POST
    @Path("/documents")
    public DocumentObject createDocument(@HeaderParam("X-Client-Id") String clientIdHeader,
                                         @HeaderParam("X-Actor") String actorHeader,
                                         @Valid CreateDocumentRequest req) {
        long clientId = RequestContext.requireClientId(clientIdHeader);
        String actor = RequestContext.actorOrSystem(actorHeader);
        return service.createDocument(clientId, actor, req);
    }
//    @GET
//    @Path("/documents")
//    public List<DocumentObject> listDocuments(@HeaderParam("X-Client-Id") String clientIdHeader,
//                                              @QueryParam("siteId") Long siteId,
//                                              @QueryParam("fileName") String fileName) {
//        long clientId = RequestContext.requireClientId(clientIdHeader);
//        return service.listDocuments(clientId, siteId, fileName);
//    }
    @GET
    @Path("/documents")
    public List<DocumentObject> getAllDocuments(
            @HeaderParam("X-Client-Id") String clientIdHeader
    ) {
        long clientId = RequestContext.requireClientId(clientIdHeader);
        return service.getAllDocuments(clientId);
    }

    // ---- Versiones por contrato ----
    @GET
    @Path("/contracts/{contractId}/versions")
    public List<ContractVersion> list(@HeaderParam("X-Client-Id") String clientIdHeader,
                                      @PathParam("contractId") long contractId) {
        long clientId = RequestContext.requireClientId(clientIdHeader);
        return service.listVersions(clientId, contractId);
    }

    @POST
    @Path("/contracts/{contractId}/versions")
    public ContractVersion createVersion(@HeaderParam("X-Client-Id") String clientIdHeader,
                                         @HeaderParam("X-Actor") String actorHeader,
                                         @PathParam("contractId") long contractId,
                                         @Valid CreateVersionRequest req) {
        long clientId = RequestContext.requireClientId(clientIdHeader);
        String actor = RequestContext.actorOrSystem(actorHeader);
        return service.createVersion(clientId, actor, contractId, req);
    }

    @GET
    @Path("/versions/{versionId}")
    public ContractVersion get(@HeaderParam("X-Client-Id") String clientIdHeader,
                               @PathParam("versionId") long versionId) {
        long clientId = RequestContext.requireClientId(clientIdHeader);
        return service.getVersion(clientId, versionId);
    }

    @PATCH
    @Path("/versions/{versionId}/status")
    public ContractVersion updateStatus(@HeaderParam("X-Client-Id") String clientIdHeader,
                                        @HeaderParam("X-Actor") String actorHeader,
                                        @PathParam("versionId") long versionId,
                                        @Valid UpdateVersionStatusRequest req) {
        long clientId = RequestContext.requireClientId(clientIdHeader);
        String actor = RequestContext.actorOrSystem(actorHeader);
        return service.updateVersionStatus(clientId, actor, versionId, req);
    }

    // Patch para cargar payloads después (OCR doc, extractionJson, modelInfoJson)
    @PATCH
    @Path("/versions/{versionId}")
    public ContractVersion patch(@HeaderParam("X-Client-Id") String clientIdHeader,
                                 @HeaderParam("X-Actor") String actorHeader,
                                 @PathParam("versionId") long versionId,
                                 Map<String, Object> patch) {
        long clientId = RequestContext.requireClientId(clientIdHeader);
        String actor = RequestContext.actorOrSystem(actorHeader);

        String extractionJson = patch.get("extractionJson") == null ? null : patch.get("extractionJson").toString();
        String modelInfoJson  = patch.get("modelInfoJson") == null ? null : patch.get("modelInfoJson").toString();
        Long ocrDocumentId    = patch.get("ocrDocumentId") == null ? null : Long.parseLong(patch.get("ocrDocumentId").toString());

        return service.patchVersionPayloads(clientId, actor, versionId, extractionJson, modelInfoJson, ocrDocumentId);
    }
}
