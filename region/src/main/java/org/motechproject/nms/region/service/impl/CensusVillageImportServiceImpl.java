package org.motechproject.nms.region.service.impl;

import org.motechproject.nms.region.domain.Taluka;
import org.motechproject.nms.region.domain.Village;
import org.motechproject.nms.region.repository.TalukaDataService;
import org.motechproject.nms.region.repository.VillageDataService;
import org.motechproject.nms.region.service.CensusVillageImportService;
import org.motechproject.nms.region.utils.GetInstanceByString;
import org.motechproject.nms.region.utils.GetLong;
import org.motechproject.nms.region.utils.GetString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.HashMap;
import java.util.Map;

@Service("censusVillageImportService")
public class CensusVillageImportServiceImpl extends BaseLocationImportService<Village> implements CensusVillageImportService {

    public static final String VILLAGE_CODE = "VCode";
    public static final String REGIONAL_NAME = "Name_G";
    public static final String NAME = "Name_E";
    public static final String TALUKA_CODE = "TCode";

    public static final String VILLAGE_CODE_FIELD = "vcode";
    public static final String REGIONAL_NAME_FIELD = "regionalName";
    public static final String NAME_FIELD = "name";
    public static final String TALUKA_CODE_FIELD = "taluka";

    private TalukaDataService talukaDataService;

    @Autowired
    public CensusVillageImportServiceImpl(VillageDataService villageDataService, TalukaDataService talukaDataService) {
        super(Village.class, villageDataService);
        this.talukaDataService = talukaDataService;
    }

    @Override
    protected Map<String, CellProcessor> getProcessorMapping() {
        Map<String, CellProcessor> mapping = new HashMap<>();
        mapping.put(VILLAGE_CODE, new GetLong());
        mapping.put(REGIONAL_NAME, new GetString());
        mapping.put(NAME, new GetString());
        mapping.put(TALUKA_CODE, new GetInstanceByString<Taluka>() {
            @Override
            public Taluka retrieve(String value) {
                return talukaDataService.findByCode(value);
            }
        });
        return mapping;
    }

    @Override
    protected Map<String, String> getFieldNameMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put(VILLAGE_CODE, VILLAGE_CODE_FIELD);
        mapping.put(REGIONAL_NAME, REGIONAL_NAME_FIELD);
        mapping.put(NAME, NAME_FIELD);
        mapping.put(TALUKA_CODE, TALUKA_CODE_FIELD);
        return mapping;
    }
}
