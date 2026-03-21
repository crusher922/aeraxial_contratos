package com.aeraxial.masterdata.dto;

import java.math.BigDecimal;

public class UpdateSpaceRequest {

    public Long siteId;
    public Long locationId;
    public String spaceCode;
    public BigDecimal areaValue;
    public String areaUnit;
    public String spaceType;
    public String description;
    public String territorialExclusivity;
    public String exclusivityNotes;
}
